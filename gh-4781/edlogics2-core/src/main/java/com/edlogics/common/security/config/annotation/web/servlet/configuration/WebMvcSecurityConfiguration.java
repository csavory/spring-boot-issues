/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.common.security.config.annotation.web.servlet.configuration;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import com.edlogics.common.security.web.bind.support.AuthenticationDetailsArgumentResolver;

/**
 * @author edlogics
 *
 */
@Configuration
@EnableWebSecurity
public class WebMvcSecurityConfiguration extends org.springframework.security.config.annotation.web.servlet.configuration.WebMvcSecurityConfiguration {

	public WebMvcSecurityConfiguration() {
		super();
	}

	@Override
	public void addArgumentResolvers( List<HandlerMethodArgumentResolver> argumentResolvers ) {
		super.addArgumentResolvers( argumentResolvers );
		argumentResolvers.add( new AuthenticationDetailsArgumentResolver() );
	}
}