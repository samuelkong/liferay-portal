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
			"lr_20StartWithWhiteSpace",
			_htmlImpl.auiCompatibleId(" StartWithWhiteSpace"));

		Assert.assertEquals(
			"ASpace20InTheMiddleOfString",
			_htmlImpl.auiCompatibleId("ASpace InTheMiddleOfString"));

		Assert.assertEquals(
			"ARegularStringWith_And0123",
			_htmlImpl.auiCompatibleId("ARegularStringWith_And0123"));

		Assert.assertEquals(
			"lr_5bStringStartsWith5b",
			_htmlImpl.auiCompatibleId("[StringStartsWith["));

		Assert.assertEquals(
			"lr__StringStartWith_",
			_htmlImpl.auiCompatibleId("_StringStartWith_"));

		Assert.assertEquals(
			"lr_bbStringStartWithRaquo",
			_htmlImpl.auiCompatibleId(CharPool.RAQUO+"StringStartWithRaquo"));

		Assert.assertEquals(
			"lr_1StringStartWithNumber",
			_htmlImpl.auiCompatibleId("1StringStartWithNumber"));

		Assert.assertEquals(
			"lr_6c49StringWithChinese5b57",
			_htmlImpl.auiCompatibleId(
				'\u6c49' + "StringWithChinese" + '\u5b57'));
	}

	private HtmlImpl _htmlImpl = new HtmlImpl();

}