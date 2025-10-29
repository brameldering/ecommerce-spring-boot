package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<ShipmentEntity, UUID> {
}
