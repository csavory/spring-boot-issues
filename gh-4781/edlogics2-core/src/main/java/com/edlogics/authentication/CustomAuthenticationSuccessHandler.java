/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

/**
 * @author jlanpher
 *
 */
@Component("customAuthenticationSuccessHandler")
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	RequestCache requestCache = new HttpSessionRequestCache();

	//todo: eventually this will just be: /ng/#onboarding
	private static final String REGISTRATION_URL = "/spa-artifacts/index.html#onboarding";

	public CustomAuthenticationSuccessHandler() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler#determineTargetUrl(
	 * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public String determineTargetUrl( HttpServletRequest request, HttpServletResponse response ) {

		SavedRequest savedRequest = requestCache.getRequest( request, response );

		if ( savedRequest == null ) {
			return super.determineTargetUrl( request, response );
		}

		/* Use the DefaultSavedRequest URL */
		String targetUrl = savedRequest.getRedirectUrl();

		/* If the target url is the login page we want to redirect the user to the home page. */
		if ( StringUtils.isEmpty( targetUrl ) || StringUtils.endsWithIgnoreCase( targetUrl, "login" ) ) {
			targetUrl = getDefaultTargetUrl();
		}
		return targetUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
	 */
	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication ) throws IOException, ServletException {

		super.onAuthenticationSuccess( request, response, authentication );
	}
}