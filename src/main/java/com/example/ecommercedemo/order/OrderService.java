package com.example.ecommercedemo.order;

import com.example.ecommercedemo.model.OrderReq;
import com.example.ecommercedemo.model.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
  Order addOrder(@NotNull(message = "Customer UUID cannot be null.") UUID customerId, @Valid OrderReq orderReq);
  List<Order> getAllOrders();
  Optional<Order> getOrderById(@NotNull(message = "Order UUID cannot be null.") UUID orderId);
  List<Order> getOrdersByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID customerId);
}
