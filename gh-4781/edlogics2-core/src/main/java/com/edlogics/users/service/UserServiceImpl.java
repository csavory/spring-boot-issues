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

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edlogics.authentication.repository.RoleRepository;
import com.edlogics.authentication.repository.UserRoleRepository;
import com.edlogics.config.ApplicationSettings;
import com.edlogics.users.domain.User;
import com.edlogics.users.repository.UserRepository;

/**
 * @author Christopher Savory
 *
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

	@Resource
	UserRepository userRepository;

	@Resource
	UserRoleRepository userRoleRepository;

	@Resource(name = "passwordEncoder")
	private PasswordEncoder passwordEncoder;

	@Resource
	private RoleRepository roleRepository;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	ApplicationSettings applicationSettings;

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public Page<User> getAllUsers( int page, int size ) {
		Sort.Order order = new Sort.Order( Sort.Direction.ASC, "lastName" ).ignoreCase();
		return userRepository.findAll( new PageRequest( page, size, new Sort( order ) ) );
	}

	@Override
	public User getUser( Long playerId ) {
		return userRepository.findOne( playerId );
	}

	@Override
	public void saveUser( User user ) {
		userRepository.save( user );
	}

	@Override
	public User getUserByEmail( String email ) {
		return this.userRepository.findByEmailIgnoreCase( email );
	}

	@Override
	public boolean doPasswordsMatch( String rawPassword, String encodedPassword ) {
		return this.passwordEncoder.matches( rawPassword, encodedPassword );
	}

}