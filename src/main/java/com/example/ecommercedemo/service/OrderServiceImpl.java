package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.mappers.OrderMapper;
import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.exceptions.ResourceNotFoundException;
import com.example.ecommercedemo.repository.OrderRepository;
import com.example.ecommercedemo.model.NewOrder;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class OrderServiceImpl implements OrderService {

  private final OrderRepository repository;

  private final OrderMapper mapper;

  public OrderServiceImpl(OrderRepository repository, OrderMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public Optional<Order> addOrder(NewOrder newOrder) {
    if (Strings.isEmpty(newOrder.getCustomerId().toString())) {
      throw new ResourceNotFoundException("Invalid customer id.");
    }
    if (Objects.isNull(newOrder.getAddress()) || Strings.isEmpty(newOrder.getAddress().getId().toString())) {
      throw new ResourceNotFoundException("Invalid address id.");
    }
    if (Objects.isNull(newOrder.getCard()) || Strings.isEmpty(newOrder.getCard().getId().toString())) {
      throw new ResourceNotFoundException("Invalid card id.");
    }
    // 1. Save Order
    return repository.insert(newOrder).map(mapper::entityToModel);
    // Ideally, here it will trigger the rest of the process
    // 2. Initiate the payment
    // 3. Once the payment is authorized, change the status to paid
    // 4. Initiate the Shipment and changed the status to Shipment Initiated or Shipped
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getOrdersByCustomerId(UUID customerId) {
     List<OrderEntity> entities = repository.findByCustomerId(customerId);
    return mapper.entityToModelList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Order> getByOrderId(UUID customerId) {
    return repository.findById(customerId).map(mapper::entityToModel);
  }
}

