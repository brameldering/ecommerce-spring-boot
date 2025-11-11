package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.mappers.AuthorizationMapper;
import com.example.ecommercedemo.model.Authorization;
import com.example.ecommercedemo.repository.OrderRepository;
import com.example.ecommercedemo.repository.PaymentRepository;
import com.example.ecommercedemo.model.PaymentReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository repository;
  private final OrderRepository orderRepo;

  private final AuthorizationMapper mapper;

  public PaymentServiceImpl(PaymentRepository repository, OrderRepository orderRepo, AuthorizationMapper mapper) {
    this.repository = repository;
    this.orderRepo = orderRepo;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public Authorization authorize(PaymentReq paymentReq) {
    return null;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Authorization> getAuthorizationByOrderId(UUID orderId) {
    return orderRepo.findById(orderId).map(OrderEntity::getAuthorizationEntity)
        .map(mapper::entityToModel);
  }
}

