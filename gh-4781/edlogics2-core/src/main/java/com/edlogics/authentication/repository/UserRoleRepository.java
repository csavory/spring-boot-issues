package com.edlogics.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

import com.edlogics.users.domain.UserRole;

@Validated
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

}