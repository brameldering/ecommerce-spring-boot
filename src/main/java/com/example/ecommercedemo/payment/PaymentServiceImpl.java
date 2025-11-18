package com.example.ecommercedemo.payment;

import com.example.ecommercedemo.order.OrderEntity;
import com.example.ecommercedemo.model.Authorization;
import com.example.ecommercedemo.order.OrderRepository;
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

