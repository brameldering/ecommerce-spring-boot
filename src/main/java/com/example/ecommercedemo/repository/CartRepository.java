package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<CartEntity, UUID> {
  @Query("select c from CartEntity c join c.user u where u.id = :customerId")
  Optional<CartEntity> findByCustomerId(@Param("customerId") UUID customerId);
}
