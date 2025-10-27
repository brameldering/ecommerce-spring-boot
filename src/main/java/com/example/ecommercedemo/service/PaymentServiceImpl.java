package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.AuthorizationEntity;
import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.repository.OrderRepository;
import com.example.ecommercedemo.repository.PaymentRepository;
import com.example.model.PaymentReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository repository;
  private final OrderRepository orderRepo;

  public PaymentServiceImpl(PaymentRepository repository, OrderRepository orderRepo) {
    this.repository = repository;
    this.orderRepo = orderRepo;
  }

  @Override
  public Optional<AuthorizationEntity> authorize(@Valid PaymentReq paymentReq) {
    return Optional.empty();
  }

  @Override
  public Optional<AuthorizationEntity> getOrdersPaymentAuthorization(@NotNull String orderId) {
    return orderRepo.findById(UUID.fromString(orderId)).map(OrderEntity::getAuthorizationEntity);
  }

  /*private AuthorizationEntity toEntity(PaymentReq m) {
    PaymentEntity e = new PaymentEntity();
    e.setAuthorized(true).setMessage()
  }*/
}

