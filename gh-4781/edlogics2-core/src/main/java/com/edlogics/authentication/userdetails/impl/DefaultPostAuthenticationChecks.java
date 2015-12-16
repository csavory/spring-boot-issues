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
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.edlogics.authentication.userdetails.CustomUserDetailsChecker;
import com.edlogics.config.ApplicationSettings;
import com.edlogics.users.domain.User;

/**
 * Encapsulates any type of validation checks which should be executed after a
 * successfully login.
 * 
 * @author jlanpher
 *
 */
@Component("defaultPostAuthenticationChecks")
public class DefaultPostAuthenticationChecks implements CustomUserDetailsChecker {

	private Logger logger = LoggerFactory.getLogger( DefaultPostAuthenticationChecks.class );

	@Resource
	MessageSource messages;

	@Autowired
	ApplicationSettings applicationSettings;

	public DefaultPostAuthenticationChecks() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetailsChecker#check(org.springframework.security.core.userdetails.UserDetails)
	 */
	@Override
	public void check( UserDetails user ) {
		if ( !user.isCredentialsNonExpired() ) {
			logger.debug( "User account credentials have expired" );

			throw new CredentialsExpiredException(
					messages.getMessage(
							ACCOUNT_CREDENTIALS_EXPIRED_MSG_KEY,
							null,
							"Your account password has expired.  Please contact support for further details.",
							LocaleContextHolder.getLocale() ) );
		}
	}

	@Override
	public void check( User user ) {
		if ( !user.isCredentialsNonExpired() ) {
			logger.debug( "User account credentials have expired" );

			throw new CredentialsExpiredException(
					messages.getMessage(
							ACCOUNT_CREDENTIALS_EXPIRED_MSG_KEY,
							null,
							"Your account password has expired.  Please contact support for further details.",
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