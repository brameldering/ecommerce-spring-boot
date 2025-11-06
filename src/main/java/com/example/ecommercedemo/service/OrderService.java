package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.OrderReq;
import com.example.ecommercedemo.model.Order;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
  Optional<Order> addOrder(OrderReq orderReq);
  List<Order> getAllOrders();
  Optional<List<Order>> getOrdersByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID customerId);
  Optional<Order> getByOrderId(@NotNull(message = "Order UUID cannot be null.") UUID id);
}
