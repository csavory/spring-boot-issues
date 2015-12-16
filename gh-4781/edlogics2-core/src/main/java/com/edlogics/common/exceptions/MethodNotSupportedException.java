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
 * @author Christopher Savory
 *
 */
@ResponseStatus(value = org.springframework.http.HttpStatus.NOT_IMPLEMENTED)
public class MethodNotSupportedException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public MethodNotSupportedException() {}

	/**
	 * @param message
	 */
	public MethodNotSupportedException( String message ) {
		super( message );
	}

	/**
	 * @param cause
	 */
	public MethodNotSupportedException( Throwable cause ) {
		super( cause );
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MethodNotSupportedException( String message, Throwable cause ) {
		super( message, cause );
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MethodNotSupportedException( String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}

}
