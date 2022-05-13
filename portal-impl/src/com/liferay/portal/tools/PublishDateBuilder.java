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

package com.liferay.portal.tools;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.xml.SAXReaderFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Yuxing Wu
 */
public class PublishDateBuilder {

	public static void main(String[] args) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(System.in));

		String xmls = bufferedReader.readLine();

		new PublishDateBuilder(StringUtil.split(xmls));
	}

	public PublishDateBuilder(String[] xmls)
		throws DocumentException, IOException {

		System.setProperty("line.separator", StringPool.NEW_LINE);

		for (String xml : xmls) {
			_addDateElement(xml);
		}
	}

	private Element _addCVPDElement(
		Element element, String groupId, String artifactId, String version) {

		Element cvpdElement = element.addElement(_CVPD);

		String date = _getCVPDDate(groupId, artifactId, version);

		if (date != null) {
			cvpdElement.setText(date);
		}

		return element;
	}

	private void _addDateElement(String xml)
		throws DocumentException, IOException {

		try {
			SAXReader saxReader = SAXReaderFactory.getSAXReader(
				null, false, false);

			Document document = saxReader.read(new File(xml));

			Element rootElement = document.getRootElement();

			Element versionElement = rootElement.element("version");

			Element librariesElement = versionElement.element("libraries");

			List<Element> libraryElements = librariesElement.elements(
				"library");

			for (Element element : libraryElements) {
				String fileNameElementText = element.elementText("file-name");

				String value = null;

				if (!fileNameElementText.startsWith("lib/")) {
					value = _getDependencyFromGradleFile(fileNameElementText);
				}
				else {
					value = _getDependencyFromPropertyFile(fileNameElementText);
				}

				String[] dependency = StringUtil.split(value, ':');

				String groupId = dependency[0];

				String artifactId = dependency[1];

				Element cvpdElement = element.element(_CVPD);

				if (cvpdElement == null) {
					String version = dependency[2];

					element = _addCVPDElement(
						element, groupId, artifactId, version);
				}

				Element lvpdElement = element.element(_LVPD);

				if (lvpdElement == null) {
					_addLVPDElement(element, groupId, artifactId);
				}
				else {
					_updateLVPDElement(lvpdElement, groupId, artifactId);
				}
			}

			_writeDocument(document, xml);
		}
		catch (DocumentException documentException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to read the " + xml, documentException);
			}

			throw new DocumentException("Unable to read the " + xml);
		}
	}

	private Element _addLVPDElement(
		Element element, String groupId, String artifactId) {

		Element lvpdElement = element.addElement(_LVPD);

		_updateLVPDElement(lvpdElement, groupId, artifactId);

		return element;
	}

	private String _extractGradleDependency(String content) {
		char[] quote = {CharPool.QUOTE, CharPool.QUOTE};

		int groupStartIndex = content.indexOf("group:");

		int groupEndIndex = content.indexOf(StringPool.COMMA, groupStartIndex);

		int nameStartIndex = content.indexOf("name:");

		int nameEndIndex = content.indexOf(StringPool.COMMA, nameStartIndex);

		int versionIndex = content.indexOf("version:");

		String groupPart = content.substring(groupStartIndex, groupEndIndex);

		String group = StringUtil.extractLast(groupPart, StringPool.COLON);

		group = StringUtil.removeChars(group.trim(), quote);

		String namePart = content.substring(nameStartIndex, nameEndIndex);

		String name = StringUtil.extractLast(namePart, StringPool.COLON);

		name = StringUtil.removeChars(name.trim(), quote);

		String versionPart = content.substring(versionIndex);

		String version = StringUtil.extractLast(versionPart, StringPool.COLON);

		version = StringUtil.removeChars(version.trim(), quote);

		return StringBundler.concat(
			group, StringPool.COLON, name, StringPool.COLON, version);
	}

	private String _formatDate(long time) {
		String format = "yyyy-MM-dd HH:mm:ss";

		DateFormat dateFormat = new SimpleDateFormat(format);

		Timestamp timestamp = new Timestamp(time);

		return dateFormat.format(timestamp);
	}

	private Map<String, File> _getBundleNameGradleFileMap() {
		File folder = new File(System.getProperty("project.modules.dir"));

		return _getBundleNameGradleFileMap(folder);
	}

	private Map<String, File> _getBundleNameGradleFileMap(File folder) {
		if (_bundleNameGradleFileMap != null) {
			return _bundleNameGradleFileMap;
		}

		_bundleNameGradleFileMap = new HashMap<>();

		File[] files = folder.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				_getBundleNameGradleFileMap(file);

				continue;
			}

			String name = file.getName();

			if (!name.equals("bnd.bnd")) {
				continue;
			}

			String bundleSymbolicName = _getBundleSymbolicName(file);

			if (bundleSymbolicName == null) {
				continue;
			}

			File gradleFile = new File(file.getParent() + "/build.gradle");

			if (!gradleFile.exists()) {
				continue;
			}

			_bundleNameGradleFileMap.put(bundleSymbolicName, gradleFile);
		}

		return _bundleNameGradleFileMap;
	}

	private String _getCVPDDate(
		String groupId, String artifactId, String version) {

		String date = null;

		JSONObject jsonObject = _requestByGroupIdAndArtifactId(
			groupId, artifactId);

		if (jsonObject == null) {
			return date;
		}

		JSONObject responseJSONObject = jsonObject.getJSONObject("response");

		int numFound = responseJSONObject.getInt("numFound");

		if (numFound != 0) {
			String key = StringBundler.concat(
				groupId, StringPool.COLON, artifactId, StringPool.COLON,
				version);

			JSONArray docsJSONArray = responseJSONObject.getJSONArray("docs");

			Iterator<Object> iterator = docsJSONArray.iterator();

			while (iterator.hasNext()) {
				JSONObject docJSONObject = (JSONObject)iterator.next();

				String id = docJSONObject.getString("id");

				if (id.equals(key)) {
					long timestamp = docJSONObject.getLong("timestamp");

					date = _formatDate(timestamp);

					break;
				}
			}
		}

		return date;
	}

	private String _getDependencyFromGradleFile(String fileNameElementText) {
		String dependency = null;

		String projectName = StringUtil.extractFirst(
			fileNameElementText, StringPool.EXCLAMATION);

		int index = projectName.lastIndexOf(StringPool.PERIOD);

		projectName = projectName.substring(0, index);

		File gradleFile = _getGradleFile(projectName);

		Path path = Paths.get(gradleFile.getAbsolutePath());

		try {
			BufferedReader reader = Files.newBufferedReader(path);

			String content;

			String fileName = StringUtil.extractLast(
				fileNameElementText, StringPool.EXCLAMATION);

			int endIndex = fileName.lastIndexOf(StringPool.PERIOD);

			fileName = fileName.substring(0, endIndex);

			StringBuilder regexSB = new StringBuilder();

			regexSB.append(".*group:.*name:\\s*\"");
			regexSB.append(fileName);
			regexSB.append("\".*version:.*");

			while ((content = reader.readLine()) != null) {
				if (content.matches(regexSB.toString())) {
					dependency = _extractGradleDependency(content);

					break;
				}
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}

		return dependency;
	}

	private String _getDependencyFromPropertyFile(String fileNameElementText) {
		String dependency = null;

		int startIndex = fileNameElementText.lastIndexOf("/");

		int endIndex = fileNameElementText.lastIndexOf(".");

		String fileName = fileNameElementText.substring(
			startIndex + 1, endIndex);

		String path = fileNameElementText.substring(0, startIndex);

		if (path.contains("/development")) {
			dependency = _dependenciesPropertiesDevelopmentFile.getProperty(
				fileName);
		}
		else if (path.contains("/portal")) {
			dependency = _dependenciesPropertiesPortalFile.getProperty(
				fileName);
		}

		return dependency;
	}

	private File _getGradleFile(String bundleName) {
		Map<String, File> bundleNameGradleFileMap =
			_getBundleNameGradleFileMap();

		return bundleNameGradleFileMap.get(bundleName);
	}

	private String _getLVPDDate(String groupId, String artifactId) {
		String date = null;

		JSONObject jsonObject = _requestByGroupIdAndArtifactId(
			groupId, artifactId);

		if (jsonObject == null) {
			return date;
		}

		JSONObject responseJSONObject = jsonObject.getJSONObject("response");

		int numFound = responseJSONObject.getInt("numFound");

		if (numFound != 0) {
			JSONArray docsJSONArray = responseJSONObject.getJSONArray("docs");

			JSONObject docJSONObject = docsJSONArray.getJSONObject(0);

			long timestamp = docJSONObject.getLong("timestamp");

			date = _formatDate(timestamp);
		}

		return date;
	}

	private String _getBundleSymbolicName(File file) {
		Path path = Paths.get(file.getAbsolutePath());

		try {
			BufferedReader reader = Files.newBufferedReader(path);

			String content;

			while ((content = reader.readLine()) != null) {
				if (StringUtil.startsWith(content, "Bundle-SymbolicName")) {
					int start = content.indexOf(StringPool.COLON);

					String name = content.substring(start + 1);

					return name.trim();
				}
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}

		return null;
	}

	private JSONObject _requestByGroupIdAndArtifactId(
		String groupId, String artifactId) {

		String key = groupId + ":" + artifactId;

		JSONObject jsonObject = _cache.get(key);

		if (jsonObject != null) {
			return jsonObject;
		}

		StringBuilder uriSB = new StringBuilder();

		uriSB.append("https://search.maven.org/solrsearch/select?q=");
		uriSB.append("g:");
		uriSB.append(groupId);
		uriSB.append("+AND+");
		uriSB.append("a:");
		uriSB.append(artifactId);
		uriSB.append("&core=gav&rows=200&wt=json");

		try {
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

			HttpGet httpGet = new HttpGet(uriSB.toString());

			CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

			HttpResponse httpResponse = closeableHttpClient.execute(httpGet);

			HttpEntity entity = httpResponse.getEntity();

			jsonObject = new JSONObject(EntityUtils.toString(entity));

			_cache.put(key, jsonObject);
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}

		return jsonObject;
	}

	private Element _updateLVPDElement(
		Element element, String groupId, String artifactId) {

		String date = _getLVPDDate(groupId, artifactId);

		if (date != null) {
			element.setText(date);
		}

		return element;
	}

	private void _writeDocument(Document document, String xml)
		throws IOException {

		try {
			OutputFormat outFormat = OutputFormat.createPrettyPrint();

			outFormat.setIndent("\t");
			outFormat.setOmitEncoding(true);
			outFormat.setExpandEmptyElements(false);
			outFormat.setPadText(false);

			OutputStream outputStream = new FileOutputStream(xml);

			XMLWriter xmlWriter = new XMLWriter(outputStream, outFormat);

			xmlWriter.write(document);
			xmlWriter.flush();
			xmlWriter.close();
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to write the " + xml, ioException);
			}

			throw new IOException("Unable to write the " + xml);
		}
	}

	private static final String _CVPD = "current-version-publish-date";

	private static final String _LVPD = "latest-version-publish-date";

	private static final Log _log = LogFactoryUtil.getLog(
		PublishDateBuilder.class);

	private static final Map<String, JSONObject> _cache = new HashMap<>();
	private static final Properties _dependenciesPropertiesDevelopmentFile;
	private static final Properties _dependenciesPropertiesPortalFile;

	static {
		String projectDir = System.getProperty("project.dir");

		_dependenciesPropertiesDevelopmentFile = new Properties();

		_dependenciesPropertiesPortalFile = new Properties();

		String developmentFilePath =
			projectDir + "/lib/development/dependencies.properties";

		String portalFilePath =
			projectDir + "/lib/portal/dependencies.properties";

		File dependenciesDevelopmentFile = new File(developmentFilePath);

		File dependenciesPortalFile = new File(portalFilePath);

		try {
			InputStream developmentFileInputStream = new BufferedInputStream(
				new FileInputStream(dependenciesDevelopmentFile));

			_dependenciesPropertiesDevelopmentFile.load(
				developmentFileInputStream);

			//add the development dependencies of versions.xml
			_dependenciesPropertiesDevelopmentFile.put(
				"ant-contrib", "ant-contrib:ant-contrib:1.0b3");
			_dependenciesPropertiesDevelopmentFile.put(
				"antelope", "com.liferay:ise.antelope:3.4.0");
			_dependenciesPropertiesDevelopmentFile.put(
				"bsh", "org.beanshell:bsh:2.0b4");
			_dependenciesPropertiesDevelopmentFile.put(
				"xmltask", "com.oopsconsultancy:xmltask:1.16");

			InputStream portalFileInputStream = new BufferedInputStream(
				new FileInputStream(dependenciesPortalFile));

			_dependenciesPropertiesPortalFile.load(portalFileInputStream);

			developmentFileInputStream.close();
			portalFileInputStream.close();
		}
		catch (FileNotFoundException fileNotFoundException) {
			_log.error(fileNotFoundException);
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}
	}

	private Map<String, File> _bundleNameGradleFileMap;

}