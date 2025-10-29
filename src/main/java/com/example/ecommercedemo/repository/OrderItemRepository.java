package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {
}
