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

package com.liferay.portal.kernel.oauth;

/**
 * @author Terry Jia
 */
public interface OAuthConstants {

	public static final int EXTRACTOR_CUSTOM = 6;

	public static final int EXTRACTOR_DEFAULT = 5;

	public static final int EXTRACTOR_JSON_OBJECT = 7;

	public static final int GET = 4;

	public static final String LABEL_EXTRACTOR_CUSTOM = "custom";

	public static final String LABEL_EXTRACTOR_DEFAULT = "default";

	public static final String LABEL_EXTRACTOR_JSON_OBJECT = "json-object";

	public static final String LABEL_SIGNATURESERVICE_HMACSHA1 = "hmacsha1";

	public static final String LABEL_SIGNATURESERVICE_PLAINTEXT = "plaintext";

	public static final String LABEL_GET = "get";

	public static final String LABEL_OAUTH_10A = "oauth-10a";

	public static final String LABEL_OAUTH_20 = "oauth-20";

	public static final String LABEL_POST = "post";

	public static final int OAUTH_10A = 1;

	public static final int OAUTH_20 = 2;

	public static final int POST = 3;

	public static final int SIGNATURESERVICE_HMACSHA1 = 8;

	public static final int SIGNATURESERVICE_PLAINTEXT = 9;

}