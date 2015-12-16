/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.common.exceptions;

/**
 * @author Christopher Savory
 *
 */
public class DuplicateDataException extends Exception {

	private static final long serialVersionUID = 1L;

	public DuplicateDataException() {}

	/**
	 * @param message
	 */
	public DuplicateDataException( String message ) {
		super( message );
	}

	/**
	 * @param cause
	 */
	public DuplicateDataException( Throwable cause ) {
		super( cause );
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DuplicateDataException( String message, Throwable cause ) {
		super( message, cause );
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public DuplicateDataException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}

}
