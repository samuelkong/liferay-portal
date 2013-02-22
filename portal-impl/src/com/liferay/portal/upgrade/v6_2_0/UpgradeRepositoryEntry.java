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

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.security.auth.FullNameGenerator;
import com.liferay.portal.security.auth.FullNameGeneratorFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * @author Mate Thurzo
 */
public class UpgradeRepositoryEntry extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			StringBundler sb = new StringBundler();

			sb.append("select Repository.companyId, repositoryEntryId from ");
			sb.append("RepositoryEntry inner join Repository on ");
			sb.append("RepositoryEntry.repositoryId = ");
			sb.append("Repository.repositoryId");

			ps = con.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			while (rs.next()) {
				long companyId = rs.getLong("companyId");
				long repositoryEntryId = rs.getLong("repositoryEntryId");

				updateRepositoryEntry(companyId, repositoryEntryId);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected Object[] getDefaultUserArray(long companyId) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select userId, firstName, middleName, lastName from User_" +
					" where companyId = ? and defaultUser = ?");

			ps.setLong(1, companyId);
			ps.setBoolean(2, true);

			rs = ps.executeQuery();

			if (rs.next()) {
				long userId = rs.getLong("userId");
				String firstName = rs.getString("firstName");
				String middleName = rs.getString("middleName");
				String lastName = rs.getString("lastName");

				Object[] defaultUserArray = new Object[2];

				defaultUserArray[0] = userId;

				FullNameGenerator fullNameGenerator =
					FullNameGeneratorFactory.getInstance();

				defaultUserArray[1] = fullNameGenerator.getFullName(
					firstName, middleName, lastName);

				return defaultUserArray;
			}

			return null;
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void updateRepositoryEntry(long companyId, long repositoryEntryId)
		throws Exception {

		Object[] defaultUserArray = getDefaultUserArray(companyId);

		if (defaultUserArray == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("No default user exists for company " + companyId);
			}

			return;
		}

		Timestamp now = new Timestamp(System.currentTimeMillis());

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"update RepositoryEntry set companyId = ?, userId = ?, " +
					"userName = ?, createDate = ?, modifiedDate = ? where " +
						"repositoryEntryId = ?");

			ps.setLong(1, companyId);
			ps.setLong(2, (Long)defaultUserArray[0]);
			ps.setString(3, (String)defaultUserArray[1]);
			ps.setTimestamp(4, now);
			ps.setTimestamp(5, now);

			ps.setLong(6, repositoryEntryId);

			ps.executeUpdate();
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		UpgradeRepositoryEntry.class);

}