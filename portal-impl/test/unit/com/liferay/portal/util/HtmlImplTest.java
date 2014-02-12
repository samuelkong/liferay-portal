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

package com.liferay.portal.util;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Neil Zhao Jin
 */
public class HtmlImplTest {

	@Test
	public void testAuiCompatibleId() {
		Assert.assertEquals(null, _htmlImpl.auiCompatibleId(null));

		Assert.assertEquals(
			StringPool.BLANK, _htmlImpl.auiCompatibleId(StringPool.BLANK));

		Assert.assertEquals(
			"2122232425262728292a2b2c2d2e2f3a3b3c3d3e3f405b5c5d5e7b7c7d7e60_",
			_htmlImpl.auiCompatibleId("!\"#$%&'()*+,-./:;<=>?@[\\]^{|}~`_"));

		StringBundler sb = new StringBundler();

		for (int i = 0; i <= 32; i++) {
			sb.append(StringPool.ASCII_TABLE[i]);
		}

		sb.append('\u007F');

		Assert.assertEquals(
			"0123456789abcdef101112131415161718191a1b1c1d1e1f207f",
			_htmlImpl.auiCompatibleId(sb.toString()));

		Assert.assertEquals(
			"non20breaking20spacea02007202f",
			_htmlImpl.auiCompatibleId(
				"non breaking space" + '\u00A0' + '\u2007' + '\u202F'));

		Assert.assertEquals("string", _htmlImpl.auiCompatibleId("string"));
	}

	private HtmlImpl _htmlImpl = new HtmlImpl();

}