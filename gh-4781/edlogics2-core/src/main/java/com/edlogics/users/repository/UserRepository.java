/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.users.repository;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

import com.edlogics.users.domain.User;

/**
 * Spring JPA for User table
 *
 * @author Christopher Savory
 *
 */
@Validated
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Retrieve a list of users by last name
	 *
	 * @param lastName
	 * @return
	 */
	List<User> findByLastName( @NotEmpty String lastName );

	/**
	 * Retrieve a user by first and last name
	 *
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	User findByFirstNameAndLastName( @NotEmpty String firstName, @NotEmpty String lastName );

	/**
	 * Retrieve a user by the users email address.
	 *
	 * @param email
	 */
	@EntityGraph(value = "User.roles", type = EntityGraphType.LOAD)
	User findByEmailIgnoreCase( @NotEmpty String email );

}