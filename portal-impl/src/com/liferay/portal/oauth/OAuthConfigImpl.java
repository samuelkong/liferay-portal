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

package com.liferay.portal.oauth;

import com.liferay.portal.kernel.oauth.OAuthConfig;
import com.liferay.portal.kernel.oauth.SignatureType;

import java.io.OutputStream;

/**
 * @author Terry Jia
 */
public class OAuthConfigImpl implements OAuthConfig {

	public OAuthConfigImpl(org.scribe.model.OAuthConfig oAuthConfig) {
		_oAuthConfig = oAuthConfig;
	}

	public OAuthConfigImpl(
		String key, String secret, String callbackURL,
		SignatureType signatureType, String scope, OutputStream outputStream) {

		_oAuthConfig = new org.scribe.model.OAuthConfig(
			key, secret, callbackURL,
			SignatureTypeTranslator.translate(signatureType), scope,
			outputStream);
	}

	@Override
	public String getScope() {
		return _oAuthConfig.getScope();
	}

	@Override
	public Object getWrappedOAuthConfig() {
		return _oAuthConfig;
	}

	private org.scribe.model.OAuthConfig _oAuthConfig;

}