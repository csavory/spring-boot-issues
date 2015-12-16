/**
 * 
 */
package com.edlogics.common.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.proxy.HibernateProxy;
import org.joda.time.DateTime;
import org.springframework.data.domain.Auditable;

import com.edlogics.users.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A base class that extends Spring JPA classes for Auditing and default Primary Keys (Long)
 * Also names the default sequence generator.
 * 
 * @author Christopher Savory
 *
 */
@MappedSuperclass
@JsonIgnoreProperties({ "createdBy", "lastModifiedBy", "createdDate", "lastModifiedDate" })
public abstract class AbstractEntity<PK extends Serializable> implements Auditable<User, PK> {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "conditional_gen", strategy = "com.edlogics.common.domain.IdKeepingSequenceGenerator")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conditional_gen")
	private PK id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by")
	private User createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "last_modified_by")
	private User lastModifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;

	@Override
	public PK getId() {

		return id;
	}

	/**
	 * Sets the id of the entity.
	 * This is intentional that it is protected as Hibernate should be the only one setting this. Please don't change it.
	 * 
	 * If you need to set an Id in a test class use:
	 * org.springframework.test.util.ReflectionTestUtils.setField( <object_ref>, "id", 1L <value> );
	 * 
	 * @param id the id to set
	 */
	protected final void setId( final PK id ) {
		this.id = id;
	}

	@Override
	public User getCreatedBy() {
		return createdBy;
	}

	@Override
	public void setCreatedBy( final User createdBy ) {
		this.createdBy = createdBy;
	}

	@Override
	public DateTime getCreatedDate() {
		return null == createdDate ? null : new DateTime( createdDate );
	}

	@Override
	public void setCreatedDate( final DateTime createdDate ) {
		this.createdDate = null == createdDate ? null : createdDate.toDate();
	}

	@Override
	public User getLastModifiedBy() {
		return lastModifiedBy;
	}

	@Override
	public void setLastModifiedBy( final User lastModifiedBy ) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@Override
	public DateTime getLastModifiedDate() {
		return null == lastModifiedDate ? null : new DateTime( lastModifiedDate );
	}

	@Override
	public void setLastModifiedDate( final DateTime lastModifiedDate ) {
		this.lastModifiedDate = null == lastModifiedDate ? null : lastModifiedDate.toDate();
	}

	@Override
	@JsonIgnore
	public boolean isNew() {
		return null == getId();
	}

	@Override
	public String toString() {
		ToStringBuilder toString = new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE );

		if ( getCreatedBy() != null && !( getCreatedBy() instanceof HibernateProxy ) ) {
			toString.append( "createdBy", getCreatedBy() );
		} else {
			toString.append( "createdBy", "<not initialized>" );
		}
		toString.append( "createdDate", getCreatedDate() );

		if ( getLastModifiedBy() != null && !( getLastModifiedBy() instanceof HibernateProxy ) ) {
			toString.append( "lastModifiedBy", getLastModifiedBy() );
		} else {
			toString.append( "lastModifiedBy", "<not initialized>" );
		}
		toString.append( "lastModifiedDate", getLastModifiedDate() );

		return toString.toString();
	}
}