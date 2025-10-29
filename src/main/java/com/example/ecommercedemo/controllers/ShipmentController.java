package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.ShipmentApi;
import com.example.ecommercedemo.model.Shipment;
import com.example.ecommercedemo.service.ShipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShipmentController implements ShipmentApi {

  private final ShipmentService service;

  public ShipmentController(ShipmentService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<List<Shipment>> getShipmentByOrderId(String id) {
    return ResponseEntity.ok(service.getShipmentsByOrderId(id));
  }
}
