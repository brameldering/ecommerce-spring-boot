package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.model.OrderReq;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryExt {
  OrderEntity insert(UUID customerId, OrderReq orderReq);
}

