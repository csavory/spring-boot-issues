/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.authentication;

import com.edlogics.users.domain.User;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author edlogics
 *
 */
public interface CustomAuthenticationProvider extends AuthenticationProvider, UserDetailsService, AuthenticationDetailsSource<HttpServletRequest, User>{

	public UserDetails createUserDetails( User user );

	public UsernamePasswordAuthenticationToken createToken( UserDetails userDetails, User user );
}