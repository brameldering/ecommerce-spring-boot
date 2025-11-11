package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.OrderApi;
import com.example.ecommercedemo.model.OrderReq;
import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.service.OrderService;
import com.example.ecommercedemo.hateoas.OrderRepresentationModelAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<Order> addOrder(UUID customerId, @Valid @RequestBody OrderReq orderReq) {
    Order createdOrder = service.addOrder(customerId, orderReq);
    return status(HttpStatus.CREATED).body(createdOrder);
  }


  @Override
  public ResponseEntity<List<Order>> getCustomerOrders(UUID customerId) {
    List<Order> orders = service.getOrdersByCustomerId(customerId);
    List<Order> ordersWithLinks = assembler.toModelList(orders);
    return ResponseEntity.ok(ordersWithLinks);
  }

  @Override
  public ResponseEntity<Order> getByOrderId(UUID orderId) {

    return service.getOrderById(orderId)
        .map(assembler::toModel) // add HATEOAS links
        .map(ResponseEntity::ok).orElse(notFound().build());
  }
}

