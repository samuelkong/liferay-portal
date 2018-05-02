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

package com.liferay.poshi.runner.elements;

import org.dom4j.Comment;

/**
 * @author Kenji Heigel
 */
public class MultilinePoshiComment extends PoshiComment {

	@Override
	public PoshiComment clone(Comment comment) {
		String commentText = comment.getText();

		if (commentText.contains("\n")) {
			return new MultilinePoshiComment(comment);
		}

		return null;
	}

	@Override
	public PoshiComment clone(String readableSyntax) {
		if (isReadableSyntaxComment(readableSyntax)) {
			return new MultilinePoshiComment(readableSyntax);
		}

		return null;
	}

	@Override
	public boolean isReadableSyntaxComment(String readableSyntax) {
		if (readableSyntax.endsWith("*/") && readableSyntax.startsWith("/*")) {
			return true;
		}

		return false;
	}

	@Override
	public void parseReadableSyntax(String readableSyntax) {
		String text = readableSyntax.substring(2, readableSyntax.length() - 2);

		setText(text);
	}

	@Override
	public String toReadableSyntax() {
		StringBuilder sb = new StringBuilder();

		sb.append("\n\t/*");
		sb.append(getText());
		sb.append("*/");

		return sb.toString();
	}

	protected MultilinePoshiComment() {
	}

	protected MultilinePoshiComment(Comment comment) {
		super(comment);
	}

	protected MultilinePoshiComment(String readableSyntax) {
		super(readableSyntax);
	}

}