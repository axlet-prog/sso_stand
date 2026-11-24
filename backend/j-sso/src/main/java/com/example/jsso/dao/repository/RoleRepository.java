package com.example.jsso.dao.repository;

import com.example.jsso.dao.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

	@Query("select r from RoleEntity r where r.code = 'USER_SSO'")
	RoleEntity getDefaultRole();
}
