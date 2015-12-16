/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.common.hibernate;

import java.sql.Types;

import org.hibernate.dialect.HSQLDialect;

/**
 * Temporary until we upgrade to Hibernate 5.0
 * 
 * @author Christopher Savory
 *
 */
public class HSQLCustomDialect extends HSQLDialect {

	/**
	 * Constructs a PostgreSQL92Dialect
	 */
	public HSQLCustomDialect() {
		super();
		this.registerColumnType( Types.JAVA_OBJECT, "varchar($l)" );
	}
}
