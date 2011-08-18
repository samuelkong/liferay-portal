/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.repository.cmis.search;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Date;

/**
 * @author Mika Koivisto
 */
public class CMISParameterValueUtil {

	public static String formatParameterValue(String field, String value) {
		return formatParameterValue(field, value, false);
	}

	public static String formatParameterValue(
		String field, String value, boolean wildcard) {

		if (field.equals(Field.CREATE_DATE)) {
			try {
				DateFormat searchSimpleDateFormat =
					DateFormatFactoryUtil.getSimpleDateFormat(
						PropsValues.INDEX_DATE_FORMAT_PATTERN);

				Date createDate = searchSimpleDateFormat.parse(value);

				DateFormat cmisSimpleDateFormat =
					DateFormatFactoryUtil.getSimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.sss'Z'");

				value = cmisSimpleDateFormat.format(createDate);
			}
			catch (ParseException pe) {
			}
		}
		else {
			value = StringUtil.replace(value, StringPool.APOSTROPHE, "\\'");
			value = StringUtil.replace(value, StringPool.UNDERLINE, "\\_");

			if (wildcard) {
				value = StringUtil.replace(value, StringPool.PERCENT, "\\%");
				value = StringPool.PERCENT.concat(value).concat(
					StringPool.PERCENT);
			}
		}

		return value;
	}

}