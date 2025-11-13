package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.PaymentApi;
import com.example.ecommercedemo.hateoas.PaymentRepresentationModelAssembler;
import com.example.ecommercedemo.model.Authorization;
import com.example.ecommercedemo.model.PaymentReq;
import com.example.ecommercedemo.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1")
public class PaymentController implements PaymentApi {

  private final PaymentService service;
  private final PaymentRepresentationModelAssembler assembler;

  private static final Logger log = LoggerFactory.getLogger(CartController.class);

  public PaymentController(PaymentService service, PaymentRepresentationModelAssembler assembler) {
    this.service = service;
    this.assembler = assembler;
  }

  @Override
  public ResponseEntity<Authorization> authorize(UUID orderId, PaymentReq paymentReq) {
    log.info("Authorize request for Payment Amount: {}", paymentReq.getAmount());
    return null;
  }

  @Override
  public ResponseEntity<Authorization> getOrdersPaymentAuthorization(UUID id) {
    log.info("Get orders for authorization id: {}", id);
    return null;
  }
}

