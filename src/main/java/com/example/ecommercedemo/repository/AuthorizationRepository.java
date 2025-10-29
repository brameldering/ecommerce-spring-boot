package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.AuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorizationRepository extends JpaRepository<AuthorizationEntity, UUID> {
}
