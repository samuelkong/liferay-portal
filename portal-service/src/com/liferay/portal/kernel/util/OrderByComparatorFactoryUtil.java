/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.portal.model.impl.BaseModelImpl;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class OrderByComparatorFactoryUtil {

	public static OrderByComparator create(
			HttpServletRequest request, Class<? extends BaseModelImpl<?>> clazz,
			String portletKey, Object... defaultOrderBycolumns)
		throws SystemException {

		return getOrderByComparatorFactory().create(
			request, clazz, portletKey, defaultOrderBycolumns);
	}

	public static OrderByComparator create(
		String tableName, Object... columns) {

		return getOrderByComparatorFactory().create(tableName, columns);
	}

	public static OrderByComparatorFactory getOrderByComparatorFactory() {
		PortalRuntimePermission.checkGetBeanProperty(
			OrderByComparatorFactoryUtil.class);

		return _orderByComparatorFactory;
	}

	public void setOrderByComparatorFactory(
		OrderByComparatorFactory orderByComparatorFactory) {

		PortalRuntimePermission.checkSetBeanProperty(getClass());

		_orderByComparatorFactory = orderByComparatorFactory;
	}

	private static OrderByComparatorFactory _orderByComparatorFactory;

}