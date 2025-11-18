package com.example.ecommercedemo.customer;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
  /**
   * Spring Data JPA automatically provides the implementation for this.
   * It's optimized to check existence without loading the entire entity.
   */
  boolean existsById(@NotNull UUID id);

  boolean existsByUsername(@NotNull String username);
}
