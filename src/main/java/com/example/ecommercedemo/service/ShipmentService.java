package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.Shipment;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface ShipmentService {
  List<Shipment> getShipmentsByOrderId(@NotNull(message = "Order UUID cannot be null.") UUID uuid);
}
