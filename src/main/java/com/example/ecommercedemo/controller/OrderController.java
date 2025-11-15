package com.example.ecommercedemo.controller;

import com.example.ecommercedemo.api.OrderApi;
import com.example.ecommercedemo.model.OrderReq;
import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.service.OrderService;
import com.example.ecommercedemo.hateoas.OrderRepresentationModelAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.status;

@RestController
@Validated
@RequestMapping("/api/v1")
public class OrderController implements OrderApi {

  private final OrderService orderService;

  private final OrderRepresentationModelAssembler orderAssembler;

  public OrderController(OrderService orderService, OrderRepresentationModelAssembler orderAssembler) {
    this.orderService = orderService;
    this.orderAssembler = orderAssembler;
  }

  @Override
  public ResponseEntity<Order> addOrder(UUID customerId, OrderReq orderReq) {
    Order createdOrder = orderService.addOrder(customerId, orderReq);
    return status(HttpStatus.CREATED).body(createdOrder);
  }


  @Override
  public ResponseEntity<List<Order>> getCustomerOrders(UUID customerId) {
    List<Order> orders = orderService.getOrdersByCustomerId(customerId);
    List<Order> ordersWithLinks = orderAssembler.toModelList(orders);
    return ResponseEntity.ok(ordersWithLinks);
  }

  @Override
  public ResponseEntity<Order> getByOrderId(UUID orderId) {

    return orderService.getOrderById(orderId)
        .map(orderAssembler::toModel) // add HATEOAS links
        .map(ResponseEntity::ok).orElse(notFound().build());
  }
}

