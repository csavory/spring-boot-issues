/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.common;

import java.util.regex.Pattern;

public class Constants {

	public static final String UTC_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	public static Pattern nonDigitsPattern = Pattern.compile( "[^0-9]+" );

	public static final String MEDIA_URL_ROOT_KEY = "mediaUrlRoot";

	public static final String MEDIA_URL_ROOT_KEY_WITH_VARIABLE_SYNTAX = "${" + MEDIA_URL_ROOT_KEY + "}";

	public static final String RESOURCE_URL_ROOT_KEY = "resourceUrlRoot";

	public static final String CONTEXT_KEY = "applicationContext";

	public static final String CONTEXT_KEY_WITH_VARIABLE_SYNTAX = "${" + CONTEXT_KEY + "}";

	public static final String HOSTNAME = "hostname";

}
