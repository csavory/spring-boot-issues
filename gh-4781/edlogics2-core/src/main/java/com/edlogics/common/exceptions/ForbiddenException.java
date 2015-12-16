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
 * This will generate a response with an HTTP Code of 403.
 * It should be used when user does not have the correct credentials for a resource
 *
 * @author Christopher Savory
 *
 */
@ResponseStatus(value = org.springframework.http.HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public ForbiddenException() {}

	/**
	 * @param message
	 */
	public ForbiddenException( String message ) {
		super( message );
	}

	/**
	 * @param cause
	 */
	public ForbiddenException( Throwable cause ) {
		super( cause );
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ForbiddenException( String message, Throwable cause ) {
		super( message, cause );
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ForbiddenException( String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}

}
