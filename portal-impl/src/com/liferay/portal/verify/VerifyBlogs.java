/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.verify;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portletdisplaytemplate.PortletDisplayTemplateHandler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.portlet.blogs.template.BlogsPortletDisplayTemplateHandler;
import com.liferay.portlet.dynamicdatamapping.model.DDMTemplate;
import com.liferay.portlet.dynamicdatamapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.util.ContentUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class VerifyBlogs extends VerifyProcess {

	protected void checkDefaultDisplayTemplates() throws Exception {
		long classNameId = PortalUtil.getClassNameId(
			BlogsEntry.class.getName());

		PortletDisplayTemplateHandler blogsDisplayTemplateHandler =
			new BlogsPortletDisplayTemplateHandler();

		List<Element> templateElements =
			blogsDisplayTemplateHandler.getDefaultTemplateElements();

		List<Company> companies = CompanyLocalServiceUtil.getCompanies();

		for (Company company : companies) {
			long companyId = company.getCompanyId();

			Group group = GroupLocalServiceUtil.getCompanyGroup(companyId);

			for (Element templateElement : templateElements) {
				String templateKey = templateElement.elementText(
					"template-key");

				DDMTemplate ddmTemplate =
					DDMTemplateLocalServiceUtil.fetchTemplate(
						group.getGroupId(), classNameId, templateKey);

				if (ddmTemplate == null) {
					continue;
				}

				String name = templateElement.elementText("name");
				String description = templateElement.elementText("description");
				String language = templateElement.elementText("language");
				String scriptFileName = templateElement.elementText(
					"script-file");
				boolean cacheable = GetterUtil.getBoolean(
					templateElement.elementText("cacheable"));

				Map<Locale, String> nameMap = new HashMap<Locale, String>();

				Locale locale = LocaleUtil.getDefault();

				nameMap.put(locale, LanguageUtil.get(locale, name));

				Map<Locale, String> descriptionMap =
					new HashMap<Locale, String>();

				descriptionMap.put(
					locale, LanguageUtil.get(locale, description));

				String script = ContentUtil.get(scriptFileName);

				ddmTemplate.setNameMap(nameMap);
				ddmTemplate.setDescriptionMap(descriptionMap);
				ddmTemplate.setLanguage(language);
				ddmTemplate.setScript(script);
				ddmTemplate.setCacheable(cacheable);

				DDMTemplateLocalServiceUtil.updateDDMTemplate(ddmTemplate);
			}
		}
	}

	@Override
	protected void doVerify() throws Exception {
		List<BlogsEntry> entries =
			BlogsEntryLocalServiceUtil.getNoAssetEntries();

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Processing " + entries.size() + " entries with no asset");
		}

		for (BlogsEntry entry : entries) {
			try {
				BlogsEntryLocalServiceUtil.updateAsset(
					entry.getUserId(), entry, null, null, null);
			}
			catch (Exception e) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to update asset for entry " +
							entry.getEntryId() + ": " + e.getMessage());
				}
			}
		}

		checkDefaultDisplayTemplates();

		if (_log.isDebugEnabled()) {
			_log.debug("Assets verified for entries");
		}
	}

	private static Log _log = LogFactoryUtil.getLog(VerifyBlogs.class);

}