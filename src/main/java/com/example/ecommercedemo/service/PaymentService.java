package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.AuthorizationEntity;
import com.example.model.PaymentReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface PaymentService {
  Optional<AuthorizationEntity> authorize(@Valid PaymentReq paymentReq);
  Optional<AuthorizationEntity> getOrdersPaymentAuthorization(@NotNull String orderId);
}