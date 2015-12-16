/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.users.service;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import com.edlogics.users.domain.User;

/**
 * Manages Users
 * 
 * @author Christopher Savory
 *
 */
@Validated
public interface UserService {

	/**
	 * Gets all the users
	 *
	 * @return list of users
	 */
	@RolesAllowed(value = { "ROLE_ADMIN" })
	public List<User> getAllUsers();

	/**
	 * Gets all the users for pagination
	 *
	 * @return list of users
	 */
	@RolesAllowed(value = { "ROLE_ADMIN" })
	public Page<User> getAllUsers( int page, int size );

	/**
	 * Returns a User by Id
	 *
	 * @param id
	 * @return the user
	 */
	public User getUser( Long id );

	/**
	 * Saves the supplied User
	 *
	 * @param user
	 */
	public void saveUser( User user );

	/**
	 * Retrieve a user by the users email address.
	 *
	 * @param email
	 */
	User getUserByEmail( @NotEmpty String email );

	/**
	 * Verify the encoded password obtained from storage matches the submitted raw password after it too is encoded.
	 * Returns true if the passwords match, false if they do not.
	 * The stored password itself is never decoded.
	 *
	 * @param rawPassword the raw password to encode and match
	 * @param encodedPassword the encoded password from storage to compare with
	 * @return true if the raw password, after encoding, matches the encoded password from storage
	 */
	boolean doPasswordsMatch( @NotNull String rawPassword, @NotNull String encodedPassword );

}