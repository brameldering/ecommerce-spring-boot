package com.example.ecommercedemo.card;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardRepository extends JpaRepository<CardEntity, UUID> {
  boolean existsByCustomerIdAndNumber(UUID customerId, String cardNumber);
}
