/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.common.http.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author edlogics
 *
 */
@Component
public class SimpleCorsFilter extends OncePerRequestFilter {

	@Value("${app.resource.url-root}")
	String resourceUrlRoot;

	private static final String TTF = "ttf";
	private static final String TTC = "ttc";
	private static final String OTF = "otf";
	private static final String EOT = "eot";
	private static final String WOFF = "woff";
	private static final String FONT_CSS = "font.css";
	private static final String CSS = "css";
	private static final String LESS = "less";

	public SimpleCorsFilter() {
		super();
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain ) throws ServletException, IOException {
		if ( shouldAddCorsHeaders( request ) ) {
			response.addHeader( "Access-Control-Allow-Origin", "*" );
			response.addHeader( "Access-Control-Allow-Methods", "GET" );
			response.addHeader( "Access-Control-Allow-Credentials", "true" );
			response.addHeader( "Access-Control-Allow-Headers", "Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers" );
			response.addHeader( "Access-Control-Expose-Headers", "Access-Control-Allow-Origin,Access-Control-Allow-Credentials" );
		}
		filterChain.doFilter( request, response );
	}

	protected boolean shouldAddCorsHeaders( HttpServletRequest request ) throws ServletException, IOException {
		String requestedUri = request.getRequestURI();
		if ( StringUtils.endsWithIgnoreCase( requestedUri, TTF ) ||
				StringUtils.endsWithIgnoreCase( requestedUri, TTC ) ||
				StringUtils.endsWithIgnoreCase( requestedUri, OTF ) ||
				StringUtils.endsWithIgnoreCase( requestedUri, EOT ) ||
				StringUtils.endsWithIgnoreCase( requestedUri, WOFF ) ||
				StringUtils.endsWithIgnoreCase( requestedUri, FONT_CSS ) ||
				StringUtils.endsWithIgnoreCase( requestedUri, CSS ) ||
				StringUtils.endsWithIgnoreCase( requestedUri, LESS ) ) {
			return true;
		}

		//Check to see if the domain is our Resource Domain
		if ( resourceUrlRoot != null ) {
			HttpRequest httpRequest = new ServletServerHttpRequest( request );
			if ( UriComponentsBuilder.fromHttpRequest( httpRequest ).build().getHost().equals( UriComponentsBuilder.fromUriString( resourceUrlRoot ).build().getHost() )
					|| UriComponentsBuilder.fromUriString( resourceUrlRoot ).build().getHost().equals( request.getHeader( "Host" ) )
					|| UriComponentsBuilder.fromUriString( resourceUrlRoot ).build().getHost().equals( request.getHeader( "Origin" ) ) ) {
				return true;
			}
		}

		return false;
	}
}