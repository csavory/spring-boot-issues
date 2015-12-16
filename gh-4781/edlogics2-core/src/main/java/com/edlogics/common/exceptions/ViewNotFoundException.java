/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.common.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Herambha Kumr P
 *
 */
@ResponseStatus(value = org.springframework.http.HttpStatus.NOT_FOUND)
public class ViewNotFoundException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public ViewNotFoundException() {}

	/**
	 * @param message
	 */
	public ViewNotFoundException( String message ) {
		super( message );
	}

	/**
	 * @param cause
	 */
	public ViewNotFoundException( Throwable cause ) {
		super( cause );
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ViewNotFoundException( String message, Throwable cause ) {
		super( message, cause );
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ViewNotFoundException( String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}

}
