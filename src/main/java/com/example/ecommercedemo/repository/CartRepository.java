package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<CartEntity, UUID> {
  //  @Query("select cart from CartEntity cart where cart.id = :customerId")
//  Optional<CartEntity> findByCustomerId(@Param("customerId") UUID customerId);
  Optional<CartEntity> findByCustomerId(UUID customerId);

  @Query("SELECT DISTINCT c FROM CartEntity c " +
      "LEFT JOIN FETCH c.items i " +  // Join cart with items (left join in case there are no items)
      "LEFT JOIN FETCH i.product p " + // Left join i
      "WHERE c.customer.id = :customerId")
  Optional<CartEntity> findCartAndItemsAndProductsByCustomerId(@Param("customerId") UUID customerId);
}

