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
 * This will generate a response with an HTTP Code of 400.
 * It should be used when there is input on the request that is not valid.
 * E.g. if a service takes First Name with a min length of 3 and "Hi" is passed in, this exception would be thrown.
 *
 * Don't use for HTML Forms because the user will be able to correct their input.
 *
 * @author Christopher Savory
 *
 */
@ResponseStatus(value = org.springframework.http.HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public BadRequestException() {}

	/**
	 * @param message
	 */
	public BadRequestException( String message ) {
		super( message );
	}

	/**
	 * @param cause
	 */
	public BadRequestException( Throwable cause ) {
		super( cause );
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BadRequestException( String message, Throwable cause ) {
		super( message, cause );
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public BadRequestException( String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}

}
