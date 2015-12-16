/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.authentication.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.hateoas.Identifiable;

import com.edlogics.authentication.GrantedAuthorityConstants;
import com.edlogics.common.domain.AbstractEntity;

/**
 * @author edlogics
 *
 */
@Entity
@Table(name = "role")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Role extends AbstractEntity<Long> implements Serializable, Identifiable<Long> {

	private static final long serialVersionUID = 126672608983834537L;

	@Column(nullable = false, unique = true)
	@Enumerated(EnumType.STRING)
	@NotNull(message = "You must provide a role.")
	private GrantedAuthorityConstants role;

	private String description;

	public Role() {
		super();
	}

	public Role( GrantedAuthorityConstants role ) {
		super();
		this.role = role;
	}

	public GrantedAuthorityConstants getRole() {
		return role;
	}

	public void setRole( GrantedAuthorityConstants role ) {
		this.role = role;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	@Override
	public final int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.append( getRole() )
				.toHashCode();
	}

	@Override
	public final boolean equals( Object obj ) {
		if ( obj == null ) {
			return false;
		}
		if ( obj == this ) {
			return true;
		}
		if ( !( obj instanceof Role ) ) {
			return false;
		}
		Role r = (Role) obj;
		return new EqualsBuilder()
				.append( getRole(), r.getRole() )
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this ).
				append( "id", getId() ).
				append( "role", getRole() ).
				toString();
	}
}