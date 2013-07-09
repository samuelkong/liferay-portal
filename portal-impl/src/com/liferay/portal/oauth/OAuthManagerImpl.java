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
import com.liferay.portal.kernel.oauth.OAuthConfig;
import com.liferay.portal.kernel.oauth.OAuthException;
import com.liferay.portal.kernel.oauth.OAuthManager;
import com.liferay.portal.kernel.oauth.OAuthRequest;
import com.liferay.portal.kernel.oauth.SignatureType;
import com.liferay.portal.kernel.oauth.Token;
import com.liferay.portal.kernel.oauth.Verb;
import com.liferay.portal.kernel.oauth.Verifier;
import com.liferay.portal.kernel.util.Validator;

import org.scribe.model.OAuthConstants;
import org.scribe.oauth.OAuthService;

/**
 * @author Brian Wing Shun Chan
 */
public class OAuthManagerImpl implements OAuthManager {

	public OAuthManagerImpl(
		String key, String secret, String accessURL, String authorizeURL,
		String requestURL, String callbackURL, String scope,
		Verb accessTokenVerb, Verb requestTokenVerb, int signatureServiceType) {

		if (Validator.isNull(callbackURL)) {
			callbackURL = OAuthConstants.OUT_OF_BAND;
		}

		OAuthConfig config = new OAuthConfigImpl(
			key, secret, callbackURL, SignatureType.Header, scope, null);

		OAuthApi api = new OAuthApiImpl(
			accessURL, authorizeURL, requestURL,
			VerbTranslator.translate(accessTokenVerb), VerbTranslator.translate(
				requestTokenVerb), signatureServiceType);

		_oAuthService =
			((org.scribe.builder.api.Api)api.getWrappedApi()).createService(
				(org.scribe.model.OAuthConfig)config.getWrappedOAuthConfig());
	}

	public OAuthManagerImpl(
		String key, String secret, String accessURL, String authorizeURL,
		String callbackURL, String scope, Verb accessTokenVerb,
		int extractorType) {

		if (Validator.isNull(callbackURL)) {
			callbackURL = OAuthConstants.OUT_OF_BAND;
		}

		OAuthConfig config = new OAuthConfigImpl(
			key, secret, callbackURL, SignatureType.Header, scope, null);

		OAuthApi api = new OAuthApiImpl(
			accessURL, authorizeURL, VerbTranslator.translate(accessTokenVerb),
			extractorType);

		_oAuthService =
			((org.scribe.builder.api.Api)api.getWrappedApi()).createService(
				(org.scribe.model.OAuthConfig)config.getWrappedOAuthConfig());
	}

	@Override
	public Token getAccessToken(Token requestToken, Verifier verifier)
		throws OAuthException {

		try {
			return new TokenImpl(
				_oAuthService.getAccessToken(
					(org.scribe.model.Token)requestToken.getWrappedToken(),
					(org.scribe.model.Verifier)verifier.getWrappedVerifier()));
		}
		catch (Exception e) {
			throw new OAuthException(e);
		}
	}

	@Override
	public String getAuthorizeURL(Token requestToken) throws OAuthException {
		try {
			return _oAuthService.getAuthorizationUrl(
				(org.scribe.model.Token)requestToken.getWrappedToken());
		}
		catch (Exception e) {
			throw new OAuthException(e);
		}
	}

	@Override
	public Token getRequestToken() throws OAuthException {
		try {
			return new TokenImpl(_oAuthService.getRequestToken());
		}
		catch (Exception e) {
			throw new OAuthException(e);
		}
	}

	@Override
	public String getVersion() throws OAuthException {
		try {
			return _oAuthService.getVersion();
		}
		catch (Exception e) {
			throw new OAuthException(e);
		}
	}

	@Override
	public void signRequest(Token accessToken, OAuthRequest oAuthRequest)
		throws OAuthException {

		try {
			_oAuthService.signRequest(
				(org.scribe.model.Token)accessToken.getWrappedToken(),
				(org.scribe.model.OAuthRequest)
					oAuthRequest.getWrappedOAuthRequest());
		}
		catch (Exception e) {
			throw new OAuthException(e);
		}
	}

	private OAuthService _oAuthService;

}