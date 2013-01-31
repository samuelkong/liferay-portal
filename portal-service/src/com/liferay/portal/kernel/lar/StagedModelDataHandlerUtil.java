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

package com.liferay.portal.kernel.lar;

import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.model.StagedModel;

/**
 * @author Brian Wing Shun Chan
 */
public class StagedModelDataHandlerUtil {

	public static <T extends StagedModel> void exportModelData(
			T stagedModel, PortletDataContext portletDataContext,
			Element... elements)
		throws PortletDataException {

		StagedModelDataHandler<T> stagedModelDataHandler =
			_getStagedModelDataHandler(stagedModel);

		stagedModelDataHandler.exportModelData(
			stagedModel, portletDataContext, elements);
	}

	public static void importModelData(
			String className, Element stagedModelElement,
			PortletDataContext portletDataContext)
		throws PortletDataException {

		StagedModelDataHandler<?> stagedModelDataHandler =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				className);

		stagedModelDataHandler.importModelData(
			stagedModelElement, portletDataContext);
	}

	public static <T extends StagedModel> void importModelData(
			T stagedModel, String path, PortletDataContext portletDataContext)
		throws PortletDataException {

		StagedModelDataHandler<T> stagedModelDataHandler =
			_getStagedModelDataHandler(stagedModel);

		stagedModelDataHandler.importModelData(
			stagedModel, path, portletDataContext);
	}

	private static <T extends StagedModel> StagedModelDataHandler<T>
		_getStagedModelDataHandler(T stagedModel) {

		BaseModel<?> baseModel = (BaseModel<?>)stagedModel;

		StagedModelDataHandler<T> stagedModelDataHandler =
			(StagedModelDataHandler<T>)
				StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
					baseModel.getModelClassName());

		return stagedModelDataHandler;
	}

}