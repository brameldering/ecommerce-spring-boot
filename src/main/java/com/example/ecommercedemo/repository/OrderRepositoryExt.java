package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.OrderEntity;
import com.example.model.NewOrder;

import java.util.Optional;

public interface OrderRepositoryExt {
  Optional<OrderEntity> insert(NewOrder m);
}

