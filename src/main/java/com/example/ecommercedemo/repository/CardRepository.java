package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardRepository extends JpaRepository<CardEntity, UUID> {
//  boolean existsByCustomerId(UUID customerId);
}
