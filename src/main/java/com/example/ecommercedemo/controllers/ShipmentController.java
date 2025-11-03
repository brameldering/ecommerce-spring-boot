package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.ShipmentApi;
import com.example.ecommercedemo.hateoas.ShipmentRepresentationModelAssembler;
import com.example.ecommercedemo.model.Shipment;
import com.example.ecommercedemo.service.ShipmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.notFound;

@RestController
@Validated
public class ShipmentController implements ShipmentApi {

  private final ShipmentService service;

  private final ShipmentRepresentationModelAssembler assembler;

  public ShipmentController(ShipmentService service, ShipmentRepresentationModelAssembler assembler) {
    this.service = service;
    this.assembler = assembler;
  }

  @Override
  public ResponseEntity<List<Shipment>> getShipmentByOrderId(UUID id) {

//    UUID uuid = UUID.fromString(id);
    List<Shipment> shipments = service.getShipmentsByOrderId(id);

    // 1. Convert List to Stream
    // 2. Map (assemble) the HATEOAS links
    // 3. Convert back to List
    List<Shipment> shipmentsWithLinks = shipments.stream()
        .map(assembler::toModel)
        .toList();

    return ResponseEntity.ok(shipmentsWithLinks);
  }
}
