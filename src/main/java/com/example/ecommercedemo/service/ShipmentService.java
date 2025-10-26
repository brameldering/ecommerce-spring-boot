package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.ShipmentEntity;
import jakarta.validation.constraints.Min;

public interface ShipmentService {
  Iterable<ShipmentEntity> getShipmentByOrderId(@Min(value = 1L, message = "Invalid order ID.")  String id);
}
