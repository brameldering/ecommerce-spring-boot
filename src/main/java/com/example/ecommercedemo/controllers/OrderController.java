package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.OrderApi;
import com.example.ecommercedemo.exceptions.OrderCreationException;
import com.example.ecommercedemo.model.NewOrder;
import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.status;

@RestController
public class OrderController implements OrderApi {

  private final OrderService service;

  public OrderController(OrderService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<Order> addOrder(NewOrder newOrder) {
    return service.addOrder(newOrder)
        .map(order -> status(HttpStatus.CREATED).body(order))
        .orElseThrow(() -> new OrderCreationException("Order creation failed"));
  }

  @Override
  public ResponseEntity<List<Order>> getOrdersByCustomerId(String customerId) {
    return ResponseEntity.ok(service.getOrdersByCustomerId(customerId));
  }

  @Override
  public ResponseEntity<Order> getByOrderId(String id) {
    return service.getByOrderId(id)
        .map(ResponseEntity::ok).orElse(notFound().build());
  }
}

