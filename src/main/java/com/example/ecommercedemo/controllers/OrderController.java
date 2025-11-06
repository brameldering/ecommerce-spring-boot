package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.OrderApi;
import com.example.ecommercedemo.exceptions.OrderCreationException;
import com.example.ecommercedemo.model.NewOrder;
import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.service.OrderService;
import com.example.ecommercedemo.hateoas.OrderRepresentationModelAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.status;

@RestController
@Validated
@RequestMapping("/api/v1")
public class OrderController implements OrderApi {

  private final OrderService service;

  private final OrderRepresentationModelAssembler assembler;

  public OrderController(OrderService service, OrderRepresentationModelAssembler assembler) {
    this.service = service;
    this.assembler = assembler;
  }

  @Override
  public ResponseEntity<Order> addOrder(NewOrder newOrder) {
    return service.addOrder(newOrder)
        .map(order -> status(HttpStatus.CREATED).body(order))
        .orElseThrow(() -> new OrderCreationException("Order creation failed"));
  }

  @Override
  public ResponseEntity<List<Order>> getAllOrders () {
    return ResponseEntity.ok(Optional.ofNullable(service.getAllOrders())
        .map(assembler::toModelList)
        .orElse(List.of()));
  }

  @Override
  public ResponseEntity<List<Order>> getCustomerOrders(UUID customerId) {
    return ResponseEntity.ok(
        service.getOrdersByCustomerId(customerId)
        .map(assembler::toModelList)
        .orElse(List.of())
    );
  }

  @Override
  public ResponseEntity<Order> getByOrderId(UUID orderId) {

    return service.getByOrderId(orderId)
        .map(assembler::toModel) // add HATEOAS links
        .map(ResponseEntity::ok).orElse(notFound().build());
  }
}

