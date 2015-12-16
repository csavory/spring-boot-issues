/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.users.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.edlogics.authentication.domain.Role;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.hateoas.Identifiable;

import com.edlogics.common.domain.AbstractEntity;

/**
 * Represents a user being in a role
 *
 * @author Christopher Savory
 *
 */
@Entity
@Audited
@AuditOverride(forClass = AbstractEntity.class, isAudited = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "role_id" }), indexes = { @Index(columnList = "role_id"), @Index(columnList = "user_id") })
public class UserRole extends AbstractEntity<Long> implements Serializable, Identifiable<Long> {

	private static final long serialVersionUID = 1L;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
	private Role role;

	protected UserRole() {
		super();
	}

	public UserRole( User user, Role role ) {
		this();
		this.user = user;
		this.role = role;

		if ( user != null && !user.getUserRoles().contains( this ) ) {
			user.getUserRoles().add( this );
		}
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @return role
	 */
	public Role getRole() {
		return this.role;
	}

	/**
	 * @param role
	 */
	public void setRole( Role role ) {
		this.role = role;
	}

	@Override
	public final int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.append( getUser() )
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
		if ( !( obj instanceof UserRole ) ) {
			return false;
		}
		UserRole ur = (UserRole) obj;
		return new EqualsBuilder()
				.append( getUser(), ur.getUser() )
				.append( getRole(), ur.getRole() )
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this ).
				append( "id", getId() ).
				append( "user", getUser() ).
				append( "role", getRole() ).
				appendSuper( super.toString() ).
				toString();
	}
}
