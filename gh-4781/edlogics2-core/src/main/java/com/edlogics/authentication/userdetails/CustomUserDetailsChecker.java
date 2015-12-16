/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.authentication.userdetails;

import org.springframework.security.core.userdetails.UserDetailsChecker;

import com.edlogics.users.domain.User;

/**
 * @author jlanpher
 *
 */
public interface CustomUserDetailsChecker extends UserDetailsChecker {

	String LOGIN_ATTEMPTS_MSG_KEY = "account.notfound.loginAttempts.exception";
	String ACCOUNT_LOCKED_MSG_KEY = "account.locked.exception";
	String ACCOUNT_DISABLED_MSG_KEY = "account.disabled.exception";
	String ACCOUNT_EXPIRED_MSG_KEY = "account.expired.exception";
	String ACCOUNT_CREDENTIALS_EXPIRED_MSG_KEY = "account.credentials.expired.exception";
	String ACCOUNT_ROLES_INCORRECT_MSG_KEY = "account.roles.incorrect.exception";
	String ACCOUNT_ROLES_NOT_ALLOWED_MSG_KEY = "account.roles.not-allowed.exception";

	/**
	 * Examines the User
	 * 
	 * @param toCheck the UserDetails instance whose status should be checked.
	 */
	void check( User toCheck );
}