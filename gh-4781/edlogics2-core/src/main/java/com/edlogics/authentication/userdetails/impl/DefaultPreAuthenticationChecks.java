/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.authentication.userdetails.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.edlogics.authentication.userdetails.CustomUserDetailsChecker;
import com.edlogics.config.ApplicationSettings;
import com.edlogics.users.domain.User;

/**
 * Encapsulates any type of validation checks which should be executed before a login attempt.
 * 
 * @author jlanpher
 *
 */
@Component("defaultPreAuthenticationChecks")
public class DefaultPreAuthenticationChecks implements CustomUserDetailsChecker {

	private Logger logger = LoggerFactory.getLogger( DefaultPreAuthenticationChecks.class );

	@Resource
	MessageSource messages;

	@Autowired
	ApplicationSettings applicationSettings;

	public DefaultPreAuthenticationChecks() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetailsChecker#check(org.springframework.security.core.userdetails.UserDetails)
	 */
	@Override
	public void check( UserDetails user ) {
		if ( !user.isAccountNonLocked() ) {
			logger.debug( "User account is locked" );

			throw new LockedException(
					messages.getMessage(
							ACCOUNT_LOCKED_MSG_KEY,
							new Object[] { "help@email.com" },
							"Your account has been locked.  Please contact support for further details.",
							LocaleContextHolder.getLocale() ) );
		}

		if ( !user.isAccountNonExpired() ) {
			logger.debug( "User account is expired" );

			throw new AccountExpiredException(
					messages.getMessage(
							ACCOUNT_EXPIRED_MSG_KEY,
							new Object[] { "help@email.com" },
							"Your account has expired.  Please contact support for further details.",
							LocaleContextHolder.getLocale() ) );
		}

		if ( !user.isEnabled() ) {
			logger.debug( "User account is disabled" );

			throw new DisabledException(
					messages.getMessage(
							ACCOUNT_DISABLED_MSG_KEY,
							new Object[] { "help@email.com" },
							"Your account has been disabled.  Please contact support for further details.",
							LocaleContextHolder.getLocale() ) );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edlogics.elrc.security.userdetails.CustomUserDetailsChecker#check(com.edlogics.elrc.domain.User)
	 */
	@Override
	public void check( User user ) {
		if ( !user.isAccountNonLocked() ) {
			logger.debug( "User account is locked" );

			throw new LockedException(
					messages.getMessage(
							ACCOUNT_LOCKED_MSG_KEY,
							new Object[] { "help@email.com" },
							"Your account has been locked.  Please contact support for further details.",
							LocaleContextHolder.getLocale() ) );
		}

		if ( !user.isAccountNonExpired() ) {
			logger.debug( "User account is expired" );

			throw new AccountExpiredException(
					messages.getMessage(
							ACCOUNT_EXPIRED_MSG_KEY,
							null,
							"Your account has expired.  Please contact support for further details.",
							LocaleContextHolder.getLocale() ) );
		}

		if ( !user.isEnabled() ) {
			logger.debug( "User account is disabled" );

			throw new DisabledException(
					messages.getMessage(
							ACCOUNT_DISABLED_MSG_KEY,
							null,
							"Your account has been disabled.  Please contact support for further details.",
							LocaleContextHolder.getLocale() ) );
		}
	}

	/**
	 * @return the messages
	 */
	public MessageSource getMessages() {
		return messages;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setMessages( MessageSource messages ) {
		this.messages = messages;
	}

	public void setApplicationSettings( ApplicationSettings applicationSettings ) {
		this.applicationSettings = applicationSettings;
	}
}