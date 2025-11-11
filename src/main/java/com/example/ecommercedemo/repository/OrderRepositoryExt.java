package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.model.OrderReq;

import java.util.Optional;

public interface OrderRepositoryExt {
  OrderEntity insert(OrderReq orderReq);
}

