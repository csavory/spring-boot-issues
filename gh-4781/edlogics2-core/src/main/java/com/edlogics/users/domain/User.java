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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.edlogics.authentication.GrantedAuthorityConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A user of the application.
 * 
 * Cannot extend AbstractEntity because that would create a recursive relationship in the hierarchy
 * 
 * @author Christopher Savory
 *
 */
@Entity
@Audited
@Table(name = "\"user\"")
@NamedEntityGraph(name = "User.roles", attributeNodes = @NamedAttributeNode("userRoles"))
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int MAX_LOGIN_ATTEMPTS = 5;

	public static enum Gender {
		MALE, FEMALE, NOTPROVIDED
	};

	@Id
	@GenericGenerator(name = "conditional_gen", strategy = "com.edlogics.common.domain.IdKeepingSequenceGenerator")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conditional_gen")
	private Long id;

	@Column(nullable = false)
	@NotEmpty(message = "You must provide a first name.")
	private String firstName;

	@Column(nullable = false)
	@NotEmpty(message = "You must provide a last name.")
	private String lastName;

	@Column(nullable = false, unique = true)
	@Email(message = "You must provide a valid email address.")
	private String email;

	@Column(nullable = false)
	@NotEmpty(message = "You must provide a password.")
	private String password;

	@Column(nullable = true, unique = true)
	private String screenName;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private final Set<UserRole> userRoles = new HashSet<>();

	private String address1;
	private String address2;
	private String city;
	private String state;

	@Column(nullable = true)
	private String phone;

	/**
	 * Used in-conjunction with spring security to allow
	 * or restrict access based upon the value of enabled.
	 */
	@Column(name = "enabled", nullable = true)
	private boolean enabled = true;

	/**
	 * Used in-conjunction with spring security to allow
	 * or restrict access based upon the value of accountNonExpired.
	 */
	@Column(name = "account_non_expired", nullable = true)
	private boolean accountNonExpired = true;

	/**
	 * Used in-conjunction with spring security to provide for the ability
	 * to expire a password based upon the value of credentialsNonExpired.
	 */
	@Column(name = "credentials_non_expired", nullable = true)
	private boolean credentialsNonExpired = true;

	/**
	 * Used in-conjunction with spring security to allow
	 * or restrict access based upon the value of accountNonLocked.
	 */
	@Column(name = "account_non_locked", nullable = true)
	private boolean accountNonLocked = true;

	@Column(name = "reset_token", nullable = true, length = 1000)
	private String resetToken;

	@Column(name = "reset_token_created", nullable = true)
	private Date resetTokenCreated;

	/**
	 * No spring JPA Annotations on the User specific properties. This is because a user might not be logged in when these are created.
	 * These properties will need to be managed in the service tier for the User object.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by")
	private User createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createdDate;

	/**
	 * No spring JPA Annotations on the User specific properties. This is because a user might not be logged in when these are created.
	 * These properties will need to be managed in the service tier for the User object.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "last_modified_by")
	private User lastModifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date lastModifiedDate;

	@Column(nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "birth_date", nullable = true)
	private Date birthDate;

	@Column(name = "postal_code", nullable = true, length = 50)
	private String postalCode;

	@Column(name = "login_attempts")
	private int loginAttempts;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, optional = true)
	@JoinColumn(name = "host_user_id")
	private User hostUser;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hostUser", cascade = CascadeType.ALL, orphanRemoval = true)
	@NotAudited
	private Set<User> dependentUsers = new HashSet<User>();

	public User() {
		super();
	}

	public User( String firstName, String lastName, String password, String email, String screenName, Gender gender, Date birthDate ) {
		super();
		this.createdDate = new Date();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.screenName = screenName;
		this.gender = gender;
		this.birthDate = birthDate;
	}

	@PrePersist
	public void dataConversion() {
		email = StringUtils.lowerCase( email );
	}

	public Long getId() {
		return this.id;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public String getEmail() {
		return this.email;
	}

	public DateTime getCreatedDate() {
		return null == createdDate ? null : new DateTime( createdDate );
	}

	public void setCreatedDate( final DateTime createdDate ) {
		this.createdDate = null == createdDate ? null : createdDate.toDate();
	}

	public String getScreenName() {
		return this.screenName;
	}

	public String getEmailAddress() {
		return getEmail();
	}

	public void setFirstName( String firstName ) {
		this.firstName = firstName;
	}

	public void setLastName( String lastName ) {
		this.lastName = lastName;
	}

	public void setEmail( String email ) {
		this.email = email;
	}

	public void setScreenName( String screenName ) {
		this.screenName = screenName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public Set<UserRole> getUserRoles() {
		return this.userRoles;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
	}

	/**
	 * @return the accountNonExpired
	 */
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	/**
	 * @param accountNonExpired the accountNonExpired to set
	 */
	public void setAccountNonExpired( boolean accountNonExpired ) {
		this.accountNonExpired = accountNonExpired;
	}

	/**
	 * @return the credentialsNonExpired
	 */
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	/**
	 * @param credentialsNonExpired the credentialsNonExpired to set
	 */
	public void setCredentialsNonExpired( boolean credentialsNonExpired ) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	/**
	 * @return the accountNonLocked
	 */
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	/**
	 * @param accountNonLocked the accountNonLocked to set
	 */
	public void setAccountNonLocked( boolean accountNonLocked ) {
		this.accountNonLocked = accountNonLocked;
	}

	/**
	 * @return the resetToken
	 */
	public String getResetToken() {
		return resetToken;
	}

	/**
	 * @param resetToken the resetToken to set
	 */
	public void setResetToken( String resetToken ) {
		this.resetToken = resetToken;
	}

	/**
	 * @return the resetTokenCreated
	 */
	public Date getResetTokenCreated() {
		return resetTokenCreated;
	}

	/**
	 * @param resetTokenCreated the resetTokenCreated to set
	 */
	public void setResetTokenCreated( Date resetTokenCreated ) {
		this.resetTokenCreated = resetTokenCreated;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender( Gender gender ) {
		this.gender = gender;
	}

	public DateTime getBirthDate() {
		return birthDate == null ? null : new DateTime( birthDate );
	}

	public void setBirthDate( DateTime birthDate ) {
		this.birthDate = birthDate == null ? null : birthDate.toDate();
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode( String postalCode ) {
		this.postalCode = postalCode;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy( User createdBy ) {
		this.createdBy = createdBy;
	}

	/**
	 * @param lastModifiedBy the lastModifiedBy to set
	 */
	public void setLastModifiedBy( User lastModifiedBy ) {
		this.lastModifiedBy = lastModifiedBy;
	}

	/**
	 * @param lastModifiedDate
	 */
	public void setLastModifiedDate( DateTime lastModifiedDate ) {
		this.lastModifiedDate = null == lastModifiedDate ? null : lastModifiedDate.toDate();
	}

	/**
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @param address1 the address1 to set
	 */
	public void setAddress1( String address1 ) {
		this.address1 = address1;
	}

	/**
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * @param address2 the address2 to set
	 */
	public void setAddress2( String address2 ) {
		this.address2 = address2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity( String city ) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState( String state ) {
		this.state = state;
	}

	/**
	 * 
	 * @return
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * 
	 * @param phone
	 */
	public void setPhone( String phone ) {
		this.phone = phone;
	}

	/**
	 * @return the loginAttempts
	 */
	public int getLoginAttempts() {
		return loginAttempts;
	}

	/**
	 * @param loginAttempts the loginAttempts to set
	 */
	public void setLoginAttempts( int loginAttempts ) {
		this.loginAttempts = loginAttempts;
	}

	/**
	 * @return the hostUser
	 */
	public User getHostUser() {
		return hostUser;
	}

	/**
	 * @param hostUser the hostUser to set
	 */
	public void setHostUser( User hostUser ) {
		this.hostUser = hostUser;
	}

	/**
	 * @return the dependentUsers
	 */
	public Set<User> getDependentUsers() {
		return dependentUsers;
	}

	/**
	 * @param dependentUsers the dependentUsers to set
	 */
	public void setDependentUsers( Set<User> dependentUsers ) {
		this.dependentUsers = dependentUsers;
	}

	public boolean isNew() {
		return null == getId();
	}

	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

	@JsonIgnore
	public int updateLoginAttempts() {
		loginAttempts++;
		if ( loginAttempts >= MAX_LOGIN_ATTEMPTS ) {
			accountNonLocked = false;
		}
		return loginAttempts;
	}

	@JsonIgnore
	public void resetLoginAttempts() {
		loginAttempts = 0;
		accountNonLocked = true;
	}

	public void addDependentUser( User user ) {
		if ( dependentUsers == null ) {
			dependentUsers = new HashSet<User>();
		}
		user.setHostUser( this );
		dependentUsers.add( user );
	}

	public boolean canReceiveEmail() {
		return isAccountNonExpired() && isEnabled();
	}

	public boolean isInternalUser() {
		for ( UserRole userRole : getUserRoles() ) {
			if ( userRole.getRole().getRole() == GrantedAuthorityConstants.ROLE_INTERNAL_USER ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.append( getFirstName() )
				.append( getLastName() )
				.append( getEmail() )
				.append( getPassword() )
				.append( getScreenName() )
				.append( getAddress1() )
				.append( getAddress2() )
				.append( getCity() )
				.append( getState() )
				.append( getPostalCode() )
				.append( getPhone() )
				.append( isAccountNonExpired() )
				.append( isAccountNonLocked() )
				.append( isCredentialsNonExpired() )
				.append( isEnabled() )
				.append( getResetToken() )
				.append( getResetTokenCreated() )
				.append( getPhone() )
				.append( getGender() )
				.append( getBirthDate() )
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
		if ( !( obj instanceof User ) ) {
			return false;
		}
		User u = (User) obj;
		return new EqualsBuilder()
				.append( getFirstName(), u.getFirstName() )
				.append( getLastName(), u.getLastName() )
				.append( getEmail(), u.getEmail() )
				.append( getPassword(), u.getPassword() )
				.append( getScreenName(), u.getScreenName() )
				.append( getAddress1(), u.getAddress1() )
				.append( getAddress2(), u.getAddress2() )
				.append( getCity(), u.getCity() )
				.append( getState(), u.getState() )
				.append( getPostalCode(), u.getPostalCode() )
				.append( getPhone(), u.getPhone() )
				.append( isAccountNonExpired(), u.isAccountNonExpired() )
				.append( isAccountNonLocked(), u.isAccountNonLocked() )
				.append( isCredentialsNonExpired(), u.isCredentialsNonExpired() )
				.append( isEnabled(), u.isEnabled() )
				.append( getResetToken(), u.getResetToken() )
				.append( getResetTokenCreated(), u.getResetTokenCreated() )
				.append( getPhone(), u.getPhone() )
				.append( getGender(), u.getGender() )
				.append( getBirthDate(), u.getBirthDate() )
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this )
				.append( "id", getId() )
				.append( "firstName", getFirstName() )
				.append( "lastName", getLastName() )
				.append( "email", getEmail() )
				.append( "password", getPassword() )
				.append( "screenName", getScreenName() )
				//.append("avatar", avatar)  //Cannot Print these values A non JPA User object is used in CustomAuthentication, which spring will log  These objects do not have a hibernate session attached and an exception is thrown
				.append( "address1", getAddress1() )
				.append( "address2", getAddress2() )
				.append( "city", getCity() )
				.append( "state", getState() )
				.append( "postalCode", getPostalCode() )
				.append( "phone", getPhone() )
				.append( "accountNonExpired", isAccountNonExpired() )
				.append( "accountNonLocked", isAccountNonLocked() )
				.append( "credentialsNonExpired", isCredentialsNonExpired() )
				.append( "enabled", isEnabled() )
				.append( "resetToken", getResetToken() )
				.append( "resetTokenCreated", getResetTokenCreated() )
				.append( "gender", getGender() )
				.append( "birthDate", getBirthDate() )
				.append( "hostUser", getHostUser() )
				.toString();
	}
}