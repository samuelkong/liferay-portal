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

package com.liferay.portal.tools.data.partitioning.sql.builder.exporter;

import com.liferay.portal.tools.data.partitioning.sql.builder.exporter.exception.DBProviderNotAvailableException;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel de la Peña
 */
public class DataPartitioningExporterFactory {

	public static DataPartitioningExporter getDataPartitioningExporter() {
		ServiceLoader<DataPartitioningExporter> serviceLoader =
			ServiceLoader.load(DataPartitioningExporter.class);

		List<DataPartitioningExporter> dataPartitioningExporters =
			new ArrayList<>();

		for (DataPartitioningExporter dataPartitioningExporter :
				serviceLoader) {

			dataPartitioningExporters.add(dataPartitioningExporter);
		}

		_logger.info(
			dataPartitioningExporters.size() +
				" data partitioning exporters available");

		for (DataPartitioningExporter dataPartitioningExporter :
				dataPartitioningExporters) {

			_logger.info(
				"Data partitioning exporter " + dataPartitioningExporter);
		}

		if (dataPartitioningExporters.isEmpty() ||
			(dataPartitioningExporters.size() > 1)) {

			throw new DBProviderNotAvailableException(
				dataPartitioningExporters.size() +
					" data partitioning exporters available");
		}

		return dataPartitioningExporters.get(0);
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		DataPartitioningExporterFactory.class);

}