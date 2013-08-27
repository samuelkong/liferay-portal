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

import com.liferay.portal.kernel.oauth.OAuthApi;
import com.liferay.portal.kernel.oauth.OAuthConstants;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.extractors.TokenExtractor20Impl;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.services.HMACSha1SignatureService;
import org.scribe.services.PlaintextSignatureService;
import org.scribe.services.SignatureService;
import org.scribe.utils.OAuthEncoder;

/**
 * @author Terry Jia
 */
public class OAuthApiImpl implements OAuthApi {

	public OAuthApiImpl(
		final String accessURL, final String authorizeURL,
		final Verb accessTokenVerb, final int accessTokenExtractorType) {

		_api = new DefaultApi20() {

			@Override
			public String getAccessTokenEndpoint() {
				return accessURL;
			}

			@Override
			public AccessTokenExtractor getAccessTokenExtractor() {
				if (accessTokenExtractorType ==
						OAuthConstants.EXTRACTOR_JSON_OBJECT) {

					return new JsonTokenExtractor();
				}
				else {
					return new TokenExtractor20Impl();
				}
			}

			@Override
			public Verb getAccessTokenVerb() {
				return accessTokenVerb;
			}

			@Override
			public String getAuthorizationUrl(OAuthConfig config) {
				if (config.hasScope()) {
					return String.format(
						authorizeURL, config.getApiKey(),
						OAuthEncoder.encode(config.getCallback()),
						OAuthEncoder.encode(config.getScope()));
				}
				else {
					return String.format(
						authorizeURL, config.getApiKey(),
						OAuthEncoder.encode(config.getCallback()));
				}
			}

		};
	}

	public OAuthApiImpl(
		final String accessURL, final String authorizeURL,
		final String requestURL, final Verb accessTokenVerb,
		final Verb requestTokenVerb, final int signatureServiceType) {

		_api = new DefaultApi10a() {

			@Override
			public String getAccessTokenEndpoint() {
				return accessURL;
			}

			@Override
			public Verb getAccessTokenVerb() {
				return accessTokenVerb;
			}

			@Override
			public String getAuthorizationUrl(Token token) {
				return String.format(authorizeURL, token.getToken());
			}

			@Override
			public String getRequestTokenEndpoint() {
				return requestURL;
			}

			@Override
			public Verb getRequestTokenVerb() {
				return requestTokenVerb;
			}

			@Override
			public SignatureService getSignatureService() {
				if (signatureServiceType ==
						OAuthConstants.SIGNATURESERVICE_PLAINTEXT) {

					return new PlaintextSignatureService();
				}
				else {
					return new HMACSha1SignatureService();
				}
			}

		};
	}

	@Override
	public Object getWrappedApi() {
		return _api;
	}

	private Api _api;

}