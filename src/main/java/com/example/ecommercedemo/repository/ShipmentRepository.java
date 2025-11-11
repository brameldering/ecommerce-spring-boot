package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<ShipmentEntity, UUID> {
  List<ShipmentEntity> findByOrderEntity_Id(UUID orderId);
}
