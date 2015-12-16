/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.common.security.config.annotation.web.servlet.configuration;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.edlogics.users.domain.User;

/**
 * @author Christopher Savory
 *
 */
public class SpringSecurityAuditorAware implements AuditorAware<User> {

	@Override
	public User getCurrentAuditor() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if ( authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken ) {
			return null;
		}

		return (User) authentication.getDetails();
	}
}