package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.Shipment;
import com.example.ecommercedemo.model.ShipmentReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface ShipmentService {
  List<Shipment> getShipmentsByOrderId(@NotNull(message = "Order UUID cannot be null.") UUID orderId);
  Shipment shipOrder (@NotNull(message = "Order UUID cannot be null.") UUID orderId, @Valid ShipmentReq shipmentReq);
}
