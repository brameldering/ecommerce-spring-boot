package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.Authorization;
import com.example.ecommercedemo.model.PaymentReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface PaymentService {
  Optional<Authorization> authorize(@Valid PaymentReq paymentReq);
  Optional<Authorization> getOrdersPaymentAuthorization(@NotNull String orderId);
}