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

  private final PaymentRepository paymentRepository;
  private final OrderRepository orderRepository;

  private final AuthorizationMapper authorizationMapper;

  public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository, AuthorizationMapper authorizationMapper) {
    this.paymentRepository = paymentRepository;
    this.orderRepository = orderRepository;
    this.authorizationMapper = authorizationMapper;
  }

  @Override
  @Transactional
  public Authorization authorize(UUID orderId, PaymentReq paymentReq) {
    return null;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Authorization> getAuthorizationByOrderId(UUID orderId) {
    return orderRepository.findById(orderId).map(OrderEntity::getAuthorizationEntity)
        .map(authorizationMapper::entityToModel);
  }
}

