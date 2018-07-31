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

package com.liferay.poshi.runner.var.scope;

import com.liferay.poshi.runner.PoshiRunner;
import com.liferay.poshi.runner.PoshiRunnerContext;
import com.liferay.poshi.runner.selenium.SeleniumUtil;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.lang3.ArrayUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Kenji Heigel
 */
public class VariableScopeTest extends TestCase {

	public void runPoshiTest(String testName) throws Exception {
		PoshiRunner poshiRunner = new PoshiRunner("LocalFile." + testName);

		poshiRunner.setUp();

		poshiRunner.test();
	}

	@Before
	public void setUp() throws Exception {
		_testBaseDir = new File(_TEST_BASE_DIR_NAME);

		if (!_testBaseDir.exists()) {
			throw new RuntimeException(
				"Test directory does not exist: " + _TEST_BASE_DIR_NAME);
		}

		String[] poshiFileNames = ArrayUtils.addAll(
			PoshiRunnerContext.POSHI_SUPPORT_FILE_INCLUDES,
			PoshiRunnerContext.POSHI_TEST_FILE_INCLUDES);

		PoshiRunnerContext.readFiles(poshiFileNames, _TEST_BASE_DIR_NAME);
	}

	@After
	public void tearDown() throws Exception {
		SeleniumUtil.stopSelenium();
	}

	@Test
	public void testAssertExecuteVarInheritance() throws Exception {
		runPoshiTest("VariableScope#ExecuteVarInheritance");
	}

	@Test
	public void testAssertRootVarInheritance() throws Exception {
		runPoshiTest("VariableScope#RootVarInheritance");
	}

	private static final String _TEST_BASE_DIR_NAME =
		"src/test/resources/com/liferay/poshi/runner/dependencies/var/scope";

	private File _testBaseDir;

}