/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.common.security.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.Authentication;

/**
 * Annotation that binds a method parameter or method return value to the {@link Authentication#getDetails()}. This is necessary to signal that the
 * argument should be resolved to the current user rather than a user that might
 * be edited on a form.
 *
 * @author edlogics
 *
 */
@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthenticationDetails {

	/**
	 * True if a {@link ClassCastException} should be thrown
	 * when the current {@link Authentication#getDetails()} is the incorrect
	 * type. Default is false.
	 *
	 * @return
	 */
	boolean errorOnInvalidType() default false;
}