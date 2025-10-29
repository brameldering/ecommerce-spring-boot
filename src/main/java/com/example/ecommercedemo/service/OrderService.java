package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.NewOrder;
import com.example.ecommercedemo.model.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface OrderService {
  Optional<Order> addOrder(@Valid NewOrder newOrder);
  List<Order> getOrdersByCustomerId(@NotNull @Valid String customerId);
  Optional<Order> getByOrderId(String id);
}
