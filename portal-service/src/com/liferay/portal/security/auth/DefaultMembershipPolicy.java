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

package com.liferay.portal.security.auth;

import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;

import java.util.Collections;
import java.util.Set;

/**
 * @author Sergio González
 */
public class DefaultMembershipPolicy implements MembershipPolicy {

	public Set<Role> getForbiddenRoles(Group group, User user) {
		return Collections.emptySet();
	}

	public Set<Role> getForbiddenRoles(Organization organization, User user) {
		return Collections.emptySet();
	}

	public Set<Role> getForbiddenRoles(User user) {
		return Collections.emptySet();
	}

	public Set<Group> getMandatoryGroups(User user) {
		return Collections.emptySet();
	}

	public Set<Role> getMandatoryRoles(Group group, User user) {
		return Collections.emptySet();
	}

	public Set<Role> getMandatoryRoles(Organization organization, User user) {
		return Collections.emptySet();
	}

	public Set<Role> getMandatoryRoles(User user) {
		return Collections.emptySet();
	}

	public boolean isApplicableUser(User user) {
		return false;
	}

	public boolean isMembershipAllowed(Group group, User user) {
		return true;
	}

}