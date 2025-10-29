package com.example.ecommercedemo.service;

import com.example.ecommercedemo.hateoas.ShipmentRepresentationModelAssembler;
import com.example.ecommercedemo.model.Shipment;
import com.example.ecommercedemo.repository.ShipmentRepository;
import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ShipmentServiceImpl implements ShipmentService {

  private final ShipmentRepository repository;

  private final ShipmentRepresentationModelAssembler assembler;

  public ShipmentServiceImpl(ShipmentRepository repository, ShipmentRepresentationModelAssembler assembler) {
    this.repository = repository;
    this.assembler = assembler;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Shipment> getShipmentsByOrderId(
      @Min(value = 1L, message = "Invalid order ID.") String id) {
    return assembler.toListModel(repository.findAllById(List.of(UUID.fromString(id))));
  }
}
