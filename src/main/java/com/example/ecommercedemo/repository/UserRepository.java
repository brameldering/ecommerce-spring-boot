package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.UserEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  /**
   * Spring Data JPA automatically provides the implementation for this.
   * It's optimized to check existence without loading the entire entity.
   */
  boolean existsById(@NotNull UUID id);
}
