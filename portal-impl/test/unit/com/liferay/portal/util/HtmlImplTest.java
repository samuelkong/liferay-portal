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
		Assert.assertEquals(null, _htmlImpl.auiCompatibleId(null));

		Assert.assertEquals(
			StringPool.BLANK, _htmlImpl.auiCompatibleId(StringPool.BLANK));

		StringBundler sb = new StringBundler();

		for (int i = 0; i <= 32; i++) {
			sb.append(StringPool.ASCII_TABLE[i]);
		}

		sb.append(CharPool.DELETE);

		StringBundler sbExpected = new StringBundler();

		sbExpected.append("_0__1__2__3__4__5__6__7__8__9__a__b__c__d__e__f_");
		sbExpected.append("_10__11__12__13__14__15__16__17__18__19__1a__1b_");
		sbExpected.append("_1c__1d__1e__1f__20__7f_");

		Assert.assertEquals(
			sbExpected.toString(), _htmlImpl.auiCompatibleId(sb.toString()));

		Assert.assertEquals(
			"_21__22__23__24__25__26__27__28__29__2a__2b__2c__2d__2e__2f__3a_",
			_htmlImpl.auiCompatibleId("!\"#$%&'()*+,-./:"));

		Assert.assertEquals(
			"_3b__3c__3d__3e__3f__40__5b__5c__5d__5e__7b__7c__7d__7e____60_",
			_htmlImpl.auiCompatibleId(";<=>?@[\\]^{|}~_`"));

		sb = new StringBundler();

		sb.append(CharPool.NO_BREAK_SPACE);
		sb.append(CharPool.FIGURE_SPACE);
		sb.append(CharPool.NARROW_NO_BREAK_SPACE);

		Assert.assertEquals(
			"_a0__2007__202f_", _htmlImpl.auiCompatibleId(sb.toString()));

		Assert.assertNotEquals(
			_htmlImpl.auiCompatibleId("205"), _htmlImpl.auiCompatibleId(" 5"));
	}

	private HtmlImpl _htmlImpl = new HtmlImpl();

}