package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.hateoas.OrderRepresentationModelAssembler;
import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.exceptions.ResourceNotFoundException;
import com.example.ecommercedemo.repository.OrderRepository;
import com.example.ecommercedemo.model.NewOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository repository;

  private final OrderRepresentationModelAssembler assembler;

  public OrderServiceImpl(OrderRepository repository, OrderRepresentationModelAssembler assembler) {
    this.repository = repository;
    this.assembler = assembler;
  }

  @Override
  @Transactional
  public Optional<Order> addOrder(@Valid NewOrder newOrder) {
    if (Strings.isEmpty(newOrder.getCustomerId())) {
      throw new ResourceNotFoundException("Invalid customer id.");
    }
    if (Objects.isNull(newOrder.getAddress()) || Strings.isEmpty(newOrder.getAddress().getId())) {
      throw new ResourceNotFoundException("Invalid address id.");
    }
    if (Objects.isNull(newOrder.getCard()) || Strings.isEmpty(newOrder.getCard().getId())) {
      throw new ResourceNotFoundException("Invalid card id.");
    }
    // 1. Save Order
    return repository.insert(newOrder).map(assembler::toModel);
    // Ideally, here it will trigger the rest of the process
    // 2. Initiate the payment
    // 3. Once the payment is authorized, change the status to paid
    // 4. Initiate the Shipment and changed the status to Shipment Initiated or Shipped
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getOrdersByCustomerId(@NotNull @Valid String customerId) {
    UUID customerUUID = UUID.fromString(customerId);
    List<OrderEntity> entities = repository.findByCustomerId(customerUUID);
    return assembler.toListModel(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Order> getByOrderId(String id) {
    UUID customerUUID = UUID.fromString(id);
    return repository.findById(customerUUID).map(assembler::toModel);
  }
}

