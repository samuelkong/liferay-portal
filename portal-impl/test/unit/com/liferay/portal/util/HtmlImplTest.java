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

import com.liferay.portal.kernel.util.CharPool;
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
		Assert.assertNull(_htmlImpl.auiCompatibleId(null));

		Assert.assertEquals(
			StringPool.BLANK, _htmlImpl.auiCompatibleId(StringPool.BLANK));

		Assert.assertEquals(
			"hello_20_world", _htmlImpl.auiCompatibleId("hello world"));

		StringBundler actual = new StringBundler();

		for (int i = 0; i <= 47; i++) {
			actual.append(StringPool.ASCII_TABLE[i]);
		}

		actual.append(":;<=>?@[\\]^_`{|}~");
		actual.append(CharPool.DELETE);
		actual.append(CharPool.NO_BREAK_SPACE);
		actual.append(CharPool.FIGURE_SPACE);
		actual.append(CharPool.NARROW_NO_BREAK_SPACE);

		StringBundler expected = new StringBundler();

		expected.append("_0__1__2__3__4__5__6__7__8__9__a__b__c__d__e__f_");
		expected.append("_10__11__12__13__14__15__16__17__18__19_");
		expected.append("_1a__1b__1c__1d__1e__1f_");
		expected.append("_20__21__22__23__24__25__26__27__28__29_");
		expected.append("_2a__2b__2c__2d__2e__2f_");
		expected.append("_3a__3b__3c__3d__3e__3f_");
		expected.append("_40_");
		expected.append("_5b__5c__5d__5e___");
		expected.append("_60_");
		expected.append("_7b__7c__7d__7e__7f_");
		expected.append("_a0__2007__202f_");

		Assert.assertEquals(
			expected.toString(), _htmlImpl.auiCompatibleId(actual.toString()));

		Assert.assertEquals(
			"hello__world", _htmlImpl.auiCompatibleId("hello_world"));
	}

	private HtmlImpl _htmlImpl = new HtmlImpl();

}