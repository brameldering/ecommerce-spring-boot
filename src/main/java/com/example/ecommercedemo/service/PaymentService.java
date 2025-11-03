package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.Authorization;
import com.example.ecommercedemo.model.PaymentReq;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
  Optional<Authorization> authorize(PaymentReq paymentReq);
  Optional<Authorization> getOrdersPaymentAuthorization(@NotNull(message = "Order UUID cannot be null.") UUID orderId);
}