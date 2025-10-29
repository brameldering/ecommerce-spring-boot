package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.Shipment;
import jakarta.validation.constraints.Min;

import java.util.List;

public interface ShipmentService {
  List<Shipment> getShipmentsByOrderId(@Min(value = 1L, message = "Invalid order ID.")  String id);
}
