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

package com.liferay.poshi.runner.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 * @author Michael Hashimoto
 */
public class ExecUtil {

	public static Process executeCommands(
			boolean exitOnFirstFail, File baseDir, long timeout,
			String... commands)
		throws IOException, TimeoutException {

		System.out.print("Executing commands: ");

		for (String command : commands) {
			System.out.println(command);
		}

		String commandTerminator = ";";

		if (exitOnFirstFail) {
			commandTerminator = "&&";
		}

		StringBuffer sb = new StringBuffer();

		for (String command : commands) {
			sb.append(command);
			sb.append(" ");
			sb.append(commandTerminator);
			sb.append(" ");
		}

		sb.append("echo Finished executing commands.\n");

		String[] bashCommands = new String[3];

		if (OSDetector.isWindows()) {
			bashCommands[0] = "CMD";
			bashCommands[1] = "/C";
		}
		else {
			bashCommands[0] = "/bin/sh";
			bashCommands[1] = "-c";
		}

		bashCommands[2] = sb.toString();

		ProcessBuilder processBuilder = new ProcessBuilder(bashCommands);

		processBuilder.directory(baseDir.getAbsoluteFile());

		Process process = processBuilder.start();

		long duration = 0;
		long start = System.currentTimeMillis();

		while (true) {
			try {
				process.exitValue();

				break;
			}
			catch (IllegalThreadStateException itse) {
				duration = System.currentTimeMillis() - start;

				if (duration >= timeout) {
					System.out.print(
						"Timeout occurred while executing Bash commands: " +
							Arrays.toString(commands));

					throw itse;
				}

				sleep(100);
			}
		}

		return process;
	}

	public static Process executeCommands(
			boolean exitOnFirstFail, String... commands)
		throws IOException, TimeoutException {

		return executeCommands(
			exitOnFirstFail, new File("."), _BASH_COMMAND_TIMEOUT_DEFAULT,
			commands);
	}

	public static Process executeCommands(String... commands)
		throws IOException, TimeoutException {

		return executeCommands(
			true, new File("."), _BASH_COMMAND_TIMEOUT_DEFAULT, commands);
	}

	public static String readInputStream(InputStream inputStream)
		throws IOException {

		StringBuffer sb = new StringBuffer();

		byte[] bytes = new byte[1024];

		int size = inputStream.read(bytes);

		while (size > 0) {
			sb.append(new String(Arrays.copyOf(bytes, size)));

			size = inputStream.read(bytes);
		}

		return sb.toString();
	}

	public static void sleep(long duration) {
		try {
			Thread.sleep(duration);
		}
		catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}

	private static final long _BASH_COMMAND_TIMEOUT_DEFAULT = 1000 * 60 * 60;

}