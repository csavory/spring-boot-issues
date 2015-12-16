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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/**
 * Filter that adds the correct content type for .json extensions
 *
 * @author Christopher Savory
 *
 */
@Component
public class SimpleFilter implements Filter {

	@Override
	public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain ) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		//Make sure the content type is set correctly for direct access to .json files
		if ( request.getRequestURI().endsWith( ".json" ) ) {
			response.setContentType( "application/json" );
		}

		chain.doFilter( req, res );
	}

	@Override
	public void init( FilterConfig filterConfig ) {}

	@Override
	public void destroy() {}

}