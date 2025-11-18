package com.example.ecommercedemo.order;

import com.example.ecommercedemo.model.OrderReq;

import java.util.UUID;

public interface OrderRepositoryExt {
  OrderEntity insert(UUID customerId, OrderReq orderReq);
}

