package com.edlogics.authentication.repository;

import javax.persistence.QueryHint;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.validation.annotation.Validated;

import com.edlogics.authentication.domain.Role;
import com.edlogics.authentication.GrantedAuthorityConstants;

@Validated
public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findById( @NotNull Long id );

	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
	Role findByRole( @NotNull GrantedAuthorityConstants roleAdmin );
}