package com.example.ecommercedemo.service;

import com.example.ecommercedemo.mappers.ShipmentMapper;
import com.example.ecommercedemo.model.Shipment;
import com.example.ecommercedemo.model.ShipmentReq;
import com.example.ecommercedemo.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
public class ShipmentServiceImpl implements ShipmentService {

  private final ShipmentRepository shipmentRepository;

  private final ShipmentMapper shipmentMapper;

  public ShipmentServiceImpl(ShipmentRepository shipmentRepository, ShipmentMapper shipmentMapper) {
    this.shipmentRepository = shipmentRepository;
    this.shipmentMapper = shipmentMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Shipment> getShipmentsByOrderId(UUID orderId) {
    return shipmentMapper.entityToModelList(shipmentRepository.findByOrderEntity_Id(orderId));
  }

  @Override
  @Transactional
  public Shipment shipOrder (UUID orderId, ShipmentReq shipmentReq) {
    return null;
  }
}
