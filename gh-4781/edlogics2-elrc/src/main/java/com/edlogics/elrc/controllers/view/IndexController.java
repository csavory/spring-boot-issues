/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.elrc.controllers.view;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.edlogics.common.exceptions.ForbiddenException;
import com.edlogics.users.service.UserService;

@Controller
@RequestMapping("/")
public class IndexController {

	protected Logger logger = LoggerFactory.getLogger( getClass() );

	@Resource
	UserService userService;

	@Autowired
	MessageSource messageSource;

	@Value("${app.timezone}")
	String elrcTimezone;

	/**
	 * Home page.
	 *
	 * @return The view name (an HTML page with Thymeleaf markup).
	 */
	@RequestMapping({ "/", "/home" })
	public ModelAndView home( HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false) Integer month, Locale locale ) {

		ModelAndView modelAndView = new ModelAndView( "home" );

		return modelAndView;
	}

	/**
	 * Tutorial page. Begins on the home page.
	 *
	 * @return The view name (an HTML page with Thymeleaf markup).
	 */
	@RequestMapping({ "/tutorial" })
	public ModelAndView tutorial( HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false) Integer month, Locale locale ) {
		ModelAndView modelAndView = home( request, response, month, locale );
		modelAndView.addObject( "startTutorial", true );

		return modelAndView;
	}

	/**
	 * Styleguide page.
	 *
	 * @return The view name (an HTML page with Thymeleaf markup).
	 */
	@RequestMapping("/styleguide")
	String styleguide() {
		return "styleguide";
	}

	/**
	 * Badge Demo page.
	 *
	 * @return The view name
	 */
	@RequestMapping("/badge-demo")
	String badgeDemo() {
		return "badge-demo";
	}

	/**
	 * Login page.
	 *
	 * @return The view name (an HTML page with Thymeleaf markup).
	 */
	@RequestMapping("/login")
	String login( Model model ) {
		return "login";
	}

	@RequestMapping("/session-expired")
	String sessionExpired( HttpServletRequest request, Locale locale ) {
		request.getSession().setAttribute( "SPRING_SECURITY_LAST_EXCEPTION", new Exception( messageSource.getMessage( "session.expired.message", null, locale ) ) );
		return "redirect:/login";
	}

	/**
	 * Used for testing purposes to see the page that happens when a 500 error occurs
	 * 
	 * @return
	 */
	@RequestMapping("/500")
	String make500Error() {
		throw new RuntimeException( "Test to create a 500 error." );
	}

	@RequestMapping("/403")
	String make403Error() {
		throw new ForbiddenException( "Test to create a 403 error." );
	}

	/**
	 * Used for extending the container's session
	 * 
	 * @return
	 */
	@RequestMapping(value = "/extend-session", produces = "application/json")
	public @ResponseBody ResponseEntity<Map<String, Boolean>> extendSession() {
		Map<String, Boolean> returnMap = new HashMap<>();
		returnMap.put( "sessionExtended", Boolean.TRUE );
		return new ResponseEntity<>( returnMap, HttpStatus.OK );
	}

}
