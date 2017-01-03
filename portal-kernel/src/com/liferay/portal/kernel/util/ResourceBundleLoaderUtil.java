/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

import com.liferay.registry.ServiceReference;
import com.liferay.registry.collections.ServiceReferenceMapper;
import com.liferay.registry.collections.ServiceTrackerCollections;
import com.liferay.registry.collections.ServiceTrackerMap;

/**
 * @author Carlos Sierra Andrés
 */
public class ResourceBundleLoaderUtil {

	public static ResourceBundleLoader getPortalResourceBundleLoader() {
		return _portalResourceBundleLoader;
	}

	public static ResourceBundleLoader
		getResourceBundleLoaderByBundleSymbolicName(String bundleSymbolicName) {

		return _instance._bundleSymbolicNameServiceTrackerMap.getService(
			bundleSymbolicName);
	}

	public static ResourceBundleLoader
		getResourceBundleLoaderByServletContextName(String servletContextName) {

		return _instance._servletContextNameServiceTrackerMap.getService(
			servletContextName);
	}

	public static ResourceBundleLoader
		getResourceBundleLoaderByServletContextNameAndBaseName(
			String servletContextName, String baseName) {

		return _instance._servletContextNameAndBaseNameServiceTrackerMap.
			getService(baseName + "#" + servletContextName);
	}

	public static void setPortalResourceBundleLoader(
		ResourceBundleLoader resourceBundleLoader) {

		_portalResourceBundleLoader = resourceBundleLoader;
	}

	private ResourceBundleLoaderUtil() {
		_bundleSymbolicNameServiceTrackerMap =
			ServiceTrackerCollections.openSingleValueMap(
				ResourceBundleLoader.class, "bundle.symbolic.name");
		_servletContextNameAndBaseNameServiceTrackerMap =
			ServiceTrackerCollections.openSingleValueMap(
				ResourceBundleLoader.class,
				"(&(resource.bundle.base.name=*)(servlet.context.name=*))",
				new ServiceReferenceMapper<String, ResourceBundleLoader>() {

					@Override
					public void map(
						ServiceReference<ResourceBundleLoader> serviceReference,
						Emitter<String> emitter) {

						Object baseName = serviceReference.getProperty(
							"resource.bundle.base.name");
						Object servletContextName =
							serviceReference.getProperty(
								"servlet.context.name");

						emitter.emit(baseName + "#" + servletContextName);
					}

				});
		_servletContextNameServiceTrackerMap =
			ServiceTrackerCollections.openSingleValueMap(
				ResourceBundleLoader.class, "servlet.context.name");
	}

	private static final ResourceBundleLoaderUtil _instance =
		new ResourceBundleLoaderUtil();

	private static ResourceBundleLoader _portalResourceBundleLoader;

	private final ServiceTrackerMap<String, ResourceBundleLoader>
		_bundleSymbolicNameServiceTrackerMap;
	private final ServiceTrackerMap<String, ResourceBundleLoader>
		_servletContextNameAndBaseNameServiceTrackerMap;
	private final ServiceTrackerMap<String, ResourceBundleLoader>
		_servletContextNameServiceTrackerMap;

}