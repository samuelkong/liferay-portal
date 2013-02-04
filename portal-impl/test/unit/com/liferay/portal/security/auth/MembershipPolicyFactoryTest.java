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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class MembershipPolicyFactoryTest {

	@Test
	public void testSetInstance() throws InterruptedException {
		Thread thread = new Thread() {

			@Override
			public void run() {
				MembershipPolicy membershipPolicy =
					new DefaultMembershipPolicy();

				while (!isInterrupted()) {
					MembershipPolicyFactory.setInstance(membershipPolicy);

					MembershipPolicyFactory.setInstance(null);
				}
			}
		};

		thread.start();

		for (int i = 0; i < 10000; i++) {
			MembershipPolicy membershipPolicy =
				MembershipPolicyFactory.getInstance();

			if (membershipPolicy == null) {
				Assert.fail("Membership policy is null");
			}
		}

		thread.interrupt();

		thread.join();
	}

}