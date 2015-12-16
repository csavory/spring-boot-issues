/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.authentication;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.edlogics.authentication.userdetails.CustomUserDetailsChecker;
import com.edlogics.users.domain.User;
import com.edlogics.users.domain.UserRole;
import com.edlogics.users.service.UserService;

/**
 * @author jlanpher
 */
@Component(value = "customAuthenticationProvider")
public class CustomAuthenticationProviderImpl implements CustomAuthenticationProvider {

	protected Logger logger = LoggerFactory.getLogger( getClass() );

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "messageSource")
	private MessageSource messages;

	@Resource(name = "defaultPreAuthenticationChecks")
	private CustomUserDetailsChecker preAuthenticationChecks;

	@Resource(name = "defaultPostAuthenticationChecks")
	private CustomUserDetailsChecker postAuthenticationChecks;

	public CustomAuthenticationProviderImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		String userName = ( authentication.getPrincipal() == null ) ? "NONE_PROVIDED" : authentication.getName();

		User user = this.userService.getUserByEmail( userName );

		if ( user == null ) {
			throw new AuthenticationCredentialsNotFoundException(
					this.messages.getMessage(
							"account.notfound.exception",
							null,
							LocaleContextHolder.getLocale() ) );
		}

		UserDetails userDetails = createUserDetails( user );

		this.preAuthenticationChecks.check( userDetails );

		additionalAuthenticationChecks( userDetails, authentication, user );

		this.postAuthenticationChecks.check( userDetails );

		return createToken( userDetails, user );
	}

	/**
	 * This method is used to perform additional authentication checks. Specifically this method is responsible
	 * for password validation.
	 *
	 * @param userDetails
	 * @param authentication
	 * @throws AuthenticationException
	 */
	protected void additionalAuthenticationChecks( UserDetails userDetails, Authentication authentication, User user ) throws AuthenticationException {
		if ( !this.userService.doPasswordsMatch( (String) authentication.getCredentials(), userDetails.getPassword() ) ) {
			user.updateLoginAttempts();

			userService.saveUser( user );

			/* As apart of capturing the number of login attempts we need to reverify if the user(s) account has been locked. */
			this.preAuthenticationChecks.check( user );

			Object[] params = new Object[] { user.getLoginAttempts(), User.MAX_LOGIN_ATTEMPTS, ( User.MAX_LOGIN_ATTEMPTS - user.getLoginAttempts() ) };

			throw new AuthenticationCredentialsNotFoundException(
					this.messages.getMessage(
							CustomUserDetailsChecker.LOGIN_ATTEMPTS_MSG_KEY,
							params,
							LocaleContextHolder.getLocale() ) );
		}
		user.resetLoginAttempts();
		userService.saveUser( user );
	}

	/**
	 * Creates an instance of {@link UsernamePasswordAuthenticationToken} which is used by spring security for all further verifications.
	 *
	 * @param userDetails
	 * @param user
	 * @return
	 */
	@Override
	public UsernamePasswordAuthenticationToken createToken( UserDetails userDetails, User user ) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				userDetails,
				userDetails.getPassword(),
				userDetails.getAuthorities() );
		token.setDetails( user );
		return token;
	}

	/**
	 * Creates the Spring Security {@link UserDetails} equivalent of the {@link com.edlogics.users.domain.User} instance.
	 * This is necessary as the user details instance is used for:
	 * <p/>
	 * 1) Additional validation of the users account status. 2) Create the {@link UserDetails} which is used to populate an instance of
	 * {@link UsernamePasswordAuthenticationToken} by spring security for all further verifications.
	 *
	 * @param user
	 * @return
	 */
	@Override
	public UserDetails createUserDetails( User user ) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for ( UserRole userRole : user.getUserRoles() ) {
			authorities.add( new SimpleGrantedAuthority( userRole.getRole().getRole().toString() ) );
		}

		org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
				user.getEmail(),
				user.getPassword(),
				user.isEnabled(),
				user.isAccountNonExpired(),
				user.isCredentialsNonExpired(),
				user.isAccountNonLocked(),
				authorities );

		return userDetails;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
	 */
	@Override
	public boolean supports( Class<?> authentication ) {
		return ( UsernamePasswordAuthenticationToken.class.isAssignableFrom( authentication ) );
	}

	/**
	 * @return the preAuthenticationChecks
	 */
	public CustomUserDetailsChecker getPreAuthenticationChecks() {
		return preAuthenticationChecks;
	}

	/**
	 * @param preAuthenticationChecks the preAuthenticationChecks to set
	 */
	public void setPreAuthenticationChecks( CustomUserDetailsChecker preAuthenticationChecks ) {
		this.preAuthenticationChecks = preAuthenticationChecks;
	}

	/**
	 * @return the postAuthenticationChecks
	 */
	public CustomUserDetailsChecker getPostAuthenticationChecks() {
		return postAuthenticationChecks;
	}

	/**
	 * @param postAuthenticationChecks the postAuthenticationChecks to set
	 */
	public void setPostAuthenticationChecks( CustomUserDetailsChecker postAuthenticationChecks ) {
		this.postAuthenticationChecks = postAuthenticationChecks;
	}

	@Override
	public UserDetails loadUserByUsername( String emailAddress ) throws UsernameNotFoundException {
		User user = this.userService.getUserByEmail( emailAddress );
		if ( user == null ) {
			throw new AuthenticationCredentialsNotFoundException(
					this.messages.getMessage( "account.notfound.exception", null, LocaleContextHolder.getLocale() ) );
		}
		return createUserDetails( user );
	}

	@Override
	public User buildDetails( HttpServletRequest servletRequest ) {

		String email = servletRequest.getParameter( "username" );
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		logger.info( "Authentication object is: %s", authentication );
		return this.userService.getUserByEmail( email );

	}
}