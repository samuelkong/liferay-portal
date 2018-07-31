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

import com.liferay.poshi.runner.util.RegexUtil;
import com.liferay.poshi.runner.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * @author Kenji Heigel
 */
public class ExecutePoshiElement extends PoshiElement {

	@Override
	public PoshiElement clone(Element element) {
		if (isElementType(_ELEMENT_NAME, element)) {
			return new ExecutePoshiElement(element);
		}

		return null;
	}

	@Override
	public PoshiElement clone(
		PoshiElement parentPoshiElement, String poshiScript) {

		if (_isElementType(parentPoshiElement, poshiScript)) {
			return new ExecutePoshiElement(parentPoshiElement, poshiScript);
		}

		return null;
	}

	@Override
	public void parsePoshiScript(String poshiScript) {
		String executeType = "macro";

		if (isValidUtilityClassName(poshiScript)) {
			executeType = "class";
		}
		else if (isValidFunctionFileName(poshiScript)) {
			executeType = "function";
		}

		if (executeType.equals("class")) {
			if (isValidUtilityClassName(poshiScript)) {
				addAttribute("class", getClassName(poshiScript));
				addAttribute("method", getCommandName(poshiScript));
			}

			String parentheticalContent = getParentheticalContent(poshiScript);

			add(PoshiNodeFactory.newPoshiNode(this, parentheticalContent));

			return;
		}

		if (poshiScript.startsWith("var ")) {
			PoshiNode returnPoshiNode = PoshiNodeFactory.newPoshiNode(
				this, poshiScript);

			if (returnPoshiNode instanceof ReturnPoshiElement) {
				add(returnPoshiNode);

				poshiScript = getValueFromAssignment(poshiScript);
			}
		}

		String executeCommandName = RegexUtil.getGroup(
			poshiScript, "([^\\s]*)\\(", 1);

		executeCommandName = executeCommandName.replace(".", "#");

		addAttribute(executeType, executeCommandName);

		String content = getParentheticalContent(poshiScript);

		if (content.length() == 0) {
			return;
		}

		List<String> assignments = new ArrayList<>();

		Matcher matcher = nestedVarAssignmentPattern.matcher(content);

		while (matcher.find()) {
			assignments.add(matcher.group());
		}

		for (String assignment : assignments) {
			assignment = assignment.trim();

			boolean functionAttributeAdded = false;

			for (String functionAttributeName : _FUNCTION_ATTRIBUTE_NAMES) {
				if (assignment.startsWith(functionAttributeName)) {
					String name = getNameFromAssignment(assignment);

					String value = getQuotedContent(assignment);

					value = StringEscapeUtils.unescapeXml(value);

					addAttribute(name, value);

					functionAttributeAdded = true;

					break;
				}
			}

			if (functionAttributeAdded) {
				continue;
			}

			if (assignment.endsWith(",")) {
				assignment = assignment.substring(0, assignment.length() - 1);
			}

			assignment = "var " + assignment + ";";

			add(PoshiNodeFactory.newPoshiNode(this, assignment));
		}
	}

	@Override
	public String toPoshiScript() {
		StringBuilder sb = new StringBuilder();

		if (attributeValue("class") != null) {
			String pad = getPad();

			sb.append("\n\n");
			sb.append(pad);
			sb.append(attributeValue("class"));
			sb.append(".");
			sb.append(attributeValue("method"));
			sb.append("(");

			for (PoshiElement poshiElement : toPoshiElements(elements())) {
				String poshiScript = poshiElement.toPoshiScript();

				if (poshiElement instanceof ArgPoshiElement) {
					sb.append(poshiScript.trim());
					sb.append(", ");

					continue;
				}
			}

			if (sb.length() > 2) {
				sb.setLength(sb.length() - 2);
			}

			sb.append(");");

			return sb.toString();
		}

		if (attributeValue("function") != null) {
			for (PoshiElementAttribute poshiElementAttribute :
					toPoshiElementAttributes(attributeList())) {

				String name = poshiElementAttribute.getName();

				if (name.equals("function")) {
					continue;
				}

				sb.append(poshiElementAttribute.toPoshiScript());
				sb.append(", ");
			}

			for (PoshiElement poshiElement : toPoshiElements(elements())) {
				String poshiScript = poshiElement.toPoshiScript();

				if (poshiElement instanceof VarPoshiElement) {
					sb.append(poshiScript.trim());
					sb.append(", ");

					continue;
				}
			}

			if (sb.length() > 2) {
				sb.setLength(sb.length() - 2);
			}

			return createFunctionPoshiScriptSnippet(sb.toString());
		}

		ReturnPoshiElement returnPoshiElement = null;

		for (PoshiElement poshiElement : toPoshiElements(elements())) {
			if (poshiElement instanceof ReturnPoshiElement) {
				returnPoshiElement = (ReturnPoshiElement)poshiElement;

				continue;
			}

			sb.append(poshiElement.toPoshiScript());
		}

		String poshiScriptSnippet = createMacroPoshiScriptSnippet(
			sb.toString());

		if (returnPoshiElement == null) {
			return poshiScriptSnippet;
		}

		return returnPoshiElement.createPoshiScriptSnippet(poshiScriptSnippet);
	}

	protected ExecutePoshiElement() {
	}

	protected ExecutePoshiElement(Element element) {
		super("execute", element);
	}

	protected ExecutePoshiElement(
		List<Attribute> attributes, List<Node> nodes) {

		this(_ELEMENT_NAME, attributes, nodes);
	}

	protected ExecutePoshiElement(
		PoshiElement parentPoshiElement, String poshiScript) {

		super("execute", parentPoshiElement, poshiScript);
	}

	protected ExecutePoshiElement(String name, Element element) {
		super(name, element);
	}

	protected ExecutePoshiElement(
		String elementName, List<Attribute> attributes, List<Node> nodes) {

		super(elementName, attributes, nodes);
	}

	protected ExecutePoshiElement(
		String name, PoshiElement parentPoshiElement, String poshiScript) {

		super(name, parentPoshiElement, poshiScript);
	}

	protected String createFunctionPoshiScriptSnippet(String content) {
		StringBuilder sb = new StringBuilder();

		String blockName = getBlockName();
		String pad = getPad();

		sb.append("\n\n");
		sb.append(pad);
		sb.append(blockName.replace("#", "."));
		sb.append("(");

		if (!content.equals("")) {
			if (content.contains("\n")) {
				content = content.replaceAll("\n", ",\n" + pad);
				content = content.replaceFirst(",", "");
				content = content + "\n" + pad;
			}
		}

		sb.append(content);

		sb.append(");");

		return sb.toString();
	}

	protected String createMacroPoshiScriptSnippet(String content) {
		StringBuilder sb = new StringBuilder();

		String blockName = getBlockName();
		String pad = getPad();

		sb.append("\n\n");
		sb.append(pad);
		sb.append(blockName.replace("#", "."));
		sb.append("(");

		Matcher matcher = nestedVarAssignmentPattern.matcher(content);

		StringBuffer formattedContent = new StringBuffer();

		while (matcher.find()) {
			String replacementString = StringUtil.combine(
				pad, matcher.group(1), ",", matcher.group(2));

			replacementString = replacementString.replace("$", "\\$");

			matcher.appendReplacement(formattedContent, replacementString);
		}

		if (formattedContent.length() > 1) {
			formattedContent.setLength(formattedContent.length() - 1);
		}

		sb.append(formattedContent.toString());

		String trimmedContent = content.trim();

		if (!trimmedContent.equals("")) {
			sb.append("\n");

			sb.append(pad);
		}

		sb.append(");");

		return sb.toString();
	}

	@Override
	protected String getBlockName() {
		if (attributeValue("function") != null) {
			return attributeValue("function");
		}

		return attributeValue("macro");
	}

	private boolean _isElementType(
		PoshiElement parentPoshiElement, String poshiScript) {

		if (parentPoshiElement instanceof ExecutePoshiElement) {
			return false;
		}

		if ((isVarAssignedToMacroInvocation(poshiScript) ||
			 isValidPoshiScriptStatement(_statementPattern, poshiScript)) &&
			!isValidPoshiScriptStatement(
				_utilityInvocationStatementPattern, poshiScript)) {

			return true;
		}

		return false;
	}

	private static final String _ELEMENT_NAME = "execute";

	private static final String[] _FUNCTION_ATTRIBUTE_NAMES =
		{"locator1", "locator2", "value1", "value2"};

	private static final String _UTILITY_INVOCATION_REGEX =
		"(echo|fail|takeScreenshot)\\(.*?\\)";

	private static final Pattern _statementPattern = Pattern.compile(
		"^" + INVOCATION_REGEX + STATEMENT_END_REGEX, Pattern.DOTALL);
	private static final Pattern _utilityInvocationStatementPattern =
		Pattern.compile("^" + _UTILITY_INVOCATION_REGEX + STATEMENT_END_REGEX);

}