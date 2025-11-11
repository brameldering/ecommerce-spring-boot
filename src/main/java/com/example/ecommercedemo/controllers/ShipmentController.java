package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.ShipmentApi;
import com.example.ecommercedemo.hateoas.ShipmentRepresentationModelAssembler;
import com.example.ecommercedemo.model.Authorization;
import com.example.ecommercedemo.model.PaymentReq;
import com.example.ecommercedemo.model.Shipment;
import com.example.ecommercedemo.model.ShipmentReq;
import com.example.ecommercedemo.service.ShipmentService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1")
public class ShipmentController implements ShipmentApi {

  private final ShipmentService service;

  private final ShipmentRepresentationModelAssembler assembler;

  private static final Logger log = LoggerFactory.getLogger(ShipmentController.class);

  public ShipmentController(ShipmentService service, ShipmentRepresentationModelAssembler assembler) {
    this.service = service;
    this.assembler = assembler;
  }

  @Override
  public ResponseEntity<List<Shipment>> getShipmentByOrderId(UUID id) {

    List<Shipment> shipments = service.getShipmentsByOrderId(id);

    // 1. Convert List to Stream
    // 2. Map (assemble) the HATEOAS links
    // 3. Convert back to List
    List<Shipment> shipmentsWithLinks = shipments.stream()
        .map(assembler::toModel)
        .toList();

    return ResponseEntity.ok(shipmentsWithLinks);
  }

  @Override
  public ResponseEntity<Shipment> shipOrder (UUID orderId, @Valid @RequestBody ShipmentReq shipmentReq) {
    log.info("Shipment request for Order Id: {}", orderId);
    return null;
  }
}
