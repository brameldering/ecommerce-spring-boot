package com.example.ecommercedemo.service;

import com.example.ecommercedemo.mappers.ShipmentMapper;
import com.example.ecommercedemo.model.Shipment;
import com.example.ecommercedemo.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
public class ShipmentServiceImpl implements ShipmentService {

  private final ShipmentRepository repository;

  private final ShipmentMapper mapper;

  public ShipmentServiceImpl(ShipmentRepository repository, ShipmentMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Shipment> getShipmentsByOrderId(UUID orderId) {
    return mapper.entityToModelList(repository.findByOrderEntity_Id(orderId));
  }
}
