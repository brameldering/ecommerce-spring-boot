package com.example.ecommercedemo.payment;

import com.example.ecommercedemo.model.Authorization;
import com.example.ecommercedemo.model.PaymentReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
  Authorization authorize(@NotNull(message = "Order UUID cannot be null.") UUID orderId, @Valid PaymentReq paymentReq);
  Optional<Authorization> getAuthorizationByOrderId(@NotNull(message = "Order UUID cannot be null.") UUID orderId);
}