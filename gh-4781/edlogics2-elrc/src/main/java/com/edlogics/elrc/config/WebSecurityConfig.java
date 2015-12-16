/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.elrc.config;

import java.security.SecureRandom;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import com.edlogics.authentication.CustomAuthenticationProvider;
import com.edlogics.common.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import com.edlogics.config.ApplicationSettings;

/**
 * Customizes Spring Security configuration.
 *
 * @author Chris Savory
 */
@Configuration
@Order(2)
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private ApplicationContext context;

	/**
	 * Want to make sure ApplicationSettings is configured first
	 */
	@Resource
	private ApplicationSettings applicationSettings;

	@Resource(name = "customAuthenticationSuccessHandler")
	private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@Autowired
	ResourceUrlEncodingFilter resourceUrlEncodingFilter;

	@Autowired
	@Lazy
	private CustomAuthenticationProvider customAuthenticationProvider;

	@Override
	protected void configure( HttpSecurity http ) throws Exception {

		if ( !applicationSettings.isLocal() ) {
			http.requiresChannel().antMatchers( "/**" ).requiresSecure();
		}

		// @formatter:off
		http
			//URL Rewriting/Encoding is off for Spring Security
			.csrf().disable()

			// See https://jira.springsource.org/browse/SPR-11496
			// For IE8 and IE9 SockJS
			// This is also needed for Adam iFrame pages
			.headers()
				.frameOptions().sameOrigin()
				.and()
			.addFilter( getSwitchUserFilter() )
			.addFilterAfter( resourceUrlEncodingFilter() , SwitchUserFilter.class)
			.formLogin()
				.loginPage("/login")
				.failureUrl("/login?error")
				.defaultSuccessUrl("/")
				.successHandler(this.customAuthenticationSuccessHandler)
				.permitAll()
				.and()
			.logout()
				.logoutSuccessUrl("/login?logout")
				.logoutUrl("/logout")
				.permitAll()
				.and()
			.authorizeRequests()
				.antMatchers("/").permitAll()
				.antMatchers("/js/**").permitAll()
				.antMatchers("/bower_components/**").permitAll()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/less/**").permitAll()
				.antMatchers("/img/**").permitAll()
				.antMatchers("/fonts/**").permitAll()
				.antMatchers("/json/**").permitAll()
				.antMatchers("/spa-artifacts/**").permitAll()
				.antMatchers("/ko-components/**").permitAll()
				.antMatchers("/healthscratch/**/*.js").permitAll()

				/* Begin: Swagger Console Permissions */
				.antMatchers("/configuration/security").permitAll()
				.antMatchers("/configuration/ui").permitAll()
				.antMatchers("/swagger-resources").permitAll()
				.antMatchers("/v2/api-docs").permitAll()
				.antMatchers("/webjars/**").permitAll()
				/* End: Swagger Console Permissions */

				/* Some API content-driven services don't require any authentication */
				.antMatchers("/message-service/**").permitAll()
				.antMatchers("/feature-service/**").permitAll()
				.antMatchers("/health-info-service/**").permitAll()
				.antMatchers("/configuration-service/**").permitAll()
				.antMatchers("/user-staging-service/**").permitAll()
				.antMatchers("/activity-service/**").permitAll()
				.antMatchers("/question-service/**").permitAll()

				.antMatchers("/email/**").permitAll() 
				.antMatchers("/sign-up/**").permitAll()
				.antMatchers("/unsubscribe/**").permitAll()
				.antMatchers("/join/**").permitAll()
				.antMatchers("/remote-user/**").permitAll()
				.antMatchers("/reports/**").hasAnyRole("ADMIN","CLIENT_ADMIN")
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/j_spring_security_switch_user").hasRole("ADMIN") //Impersonate user URL
				.antMatchers("/j_spring_security_exit_user").permitAll() //Quit impersonation
				.antMatchers("/registration/**").permitAll()
				.antMatchers("/profile-service/avatars").permitAll()
				.antMatchers("/account/send-password-reset").permitAll()
				.antMatchers("/account/reset-password/**").permitAll()
				.antMatchers("/account/register/**").permitAll()
				.antMatchers("/legal/terms").permitAll()
				.antMatchers("/legal/privacy").permitAll()
				.antMatchers("/help").permitAll()
				.antMatchers("/rss").permitAll()
				.antMatchers("/health").permitAll()
				.antMatchers("/session-expired").permitAll()
				.anyRequest().fullyAuthenticated()
				.and();
		// @formatter:on
	}

	@Override
	protected void configure( AuthenticationManagerBuilder auth ) throws Exception {
		auth.authenticationProvider( this.context.getBean( CustomAuthenticationProvider.class ) );
	}

	@Override
	@Autowired
	public void setApplicationContext( ApplicationContext context ) {
		super.setApplicationContext( context );
		this.context = context;
	}

	@Bean(name = "passwordEncoder")
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder( 10 );
	}

	@Bean(name = "switchUserFilter")
	public SwitchUserFilter getSwitchUserFilter() {
		SwitchUserFilter switchUserFilter = new SwitchUserFilter();
		switchUserFilter.setUserDetailsService( customAuthenticationProvider );
		switchUserFilter.setAuthenticationDetailsSource( customAuthenticationProvider );
		switchUserFilter.setTargetUrl( "/" );
		return switchUserFilter;
	}

	@Bean
	public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
		return new ResourceUrlEncodingFilter();
	}

	@Bean(name = "tokenService")
	public TokenService getTokenService() {
		KeyBasedPersistenceTokenService tokenService = new KeyBasedPersistenceTokenService();
		tokenService.setServerSecret( "!3dL061c$#P7@tF0rW" );
		tokenService.setSecureRandom( new SecureRandom() );
		tokenService.setServerInteger( new Integer( 5178 ) );
		/*
		 * Produces a token key roughly of a length == 280 characters.
		 * 
		 * With a url length limitation of roughly 669 characters this is the maximum size token key to generate
		 * and still allow for 389 characters for the base url string and any additional parameters.
		 */
		tokenService.setPseudoRandomNumberBytes( 32 );
		return tokenService;
	}
}