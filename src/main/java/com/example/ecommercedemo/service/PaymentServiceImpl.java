package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.hateoas.AuthorizationRepresentationModelAssembler;
import com.example.ecommercedemo.model.Authorization;
import com.example.ecommercedemo.repository.OrderRepository;
import com.example.ecommercedemo.repository.PaymentRepository;
import com.example.ecommercedemo.model.PaymentReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository repository;
  private final OrderRepository orderRepo;

  private final AuthorizationRepresentationModelAssembler assembler;

  public PaymentServiceImpl(PaymentRepository repository, OrderRepository orderRepo, AuthorizationRepresentationModelAssembler assembler) {
    this.repository = repository;
    this.orderRepo = orderRepo;
    this.assembler = assembler;
  }

  @Override
  @Transactional
  public Optional<Authorization> authorize(@Valid PaymentReq paymentReq) {
    return Optional.empty();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Authorization> getOrdersPaymentAuthorization(@NotNull String orderId) {
    return orderRepo.findById(UUID.fromString(orderId)).map(OrderEntity::getAuthorizationEntity)
        .map(assembler::toModel);
  }

  /*private AuthorizationEntity toEntity(PaymentReq m) {
    PaymentEntity e = new PaymentEntity();
    e.setAuthorized(true).setMessage()
  }*/
}

