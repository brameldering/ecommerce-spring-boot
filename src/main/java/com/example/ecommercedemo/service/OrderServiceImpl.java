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
  public Optional<Order> addOrder(OrderReq orderReq) {

    // Check whether customerid is valid
    Optional.ofNullable(orderReq.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Invalid customer id."));

    // Check whether address has been assigned to the order
    Optional.ofNullable(orderReq.getAddressId())
        .orElseThrow(() -> new ResourceNotFoundException("Invalid address."));

    // Check whether card has been assigned to the order
    Optional.ofNullable(orderReq.getCardId())
        .orElseThrow(() -> new ResourceNotFoundException("Invalid card."));

    // 1. Save Order
    return repository.insert(orderReq).map(mapper::entityToModel);
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
  public Optional<List<Order>> getOrdersByCustomerId(UUID customerId) {
    // 1. Get the list of OrderEntity objects (returns List<OrderEntity>, potentially empty but never null)
    List<OrderEntity> orderEntities = repository.findByCustomerId(customerId);

    // 2. Map the List of Entities to a List of Models using the mapper
    List<Order> orders = mapper.entityToModelList(orderEntities);

    // 3. Wrap the resulting (non-null) List<Order> in an Optional
    return Optional.of(orders);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Order> getByOrderId(UUID customerId) {
    return repository.findById(customerId).map(mapper::entityToModel);
  }
}

