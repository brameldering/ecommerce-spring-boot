package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.mappers.OrderMapper;
import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.exceptions.ResourceNotFoundException;
import com.example.ecommercedemo.repository.OrderRepository;
import com.example.ecommercedemo.model.OrderReq;

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
  public Order addOrder(OrderReq orderReq) {

    if (Objects.isNull(orderReq)) {
      throw new IllegalArgumentException("Order cannot be null.");
    }
    if (Objects.isNull(orderReq.getCustomerId())) {
      throw new IllegalArgumentException("Customer ID cannot be null.");
    }
    if (Objects.isNull(orderReq.getAddressId())) {
      throw new IllegalArgumentException("Address ID cannot be null.");
    }
    if (Objects.isNull(orderReq.getCardId())) {
      throw new IllegalArgumentException("Card ID cannot be null.");
    }

    // 1. Add HATEOAS links
    OrderEntity createdOrderEntity = repository.insert(orderReq);
    return mapper.entityToModel(createdOrderEntity);
    // Ideally, here it will trigger the rest of the process
    // 2. Initiate the payment
    // 3. Once the payment is authorized, change the status to paid
    // 4. Initiate the Shipment and changed the status to Shipment Initiated or Shipped
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrders() {
    return mapper.entityToModelList(repository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Order> getOrderById(UUID orderId) {
    return repository.findById(orderId).map(mapper::entityToModel);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getOrdersByCustomerId(UUID customerId) {
    // 1. Get the list of OrderEntity objects (returns List<OrderEntity>, potentially empty but never null)
    List<OrderEntity> orderEntities = repository.findByCustomerId(customerId);

    // 2. Map the List of Entities to a List of Models using the mapper
    return mapper.entityToModelList(orderEntities);
  }
}

