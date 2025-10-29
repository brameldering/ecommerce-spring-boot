package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.PaymentApi;
import com.example.ecommercedemo.hateoas.PaymentRepresentationModelAssembler;
import com.example.ecommercedemo.model.Authorization;
import com.example.ecommercedemo.model.PaymentReq;
import com.example.ecommercedemo.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController implements PaymentApi {

  private final PaymentService service;
  private final PaymentRepresentationModelAssembler assembler;

  public PaymentController(PaymentService service, PaymentRepresentationModelAssembler assembler) {
    this.service = service;
    this.assembler = assembler;
  }

  @Override
  public ResponseEntity<Authorization> authorize(PaymentReq paymentReq) {
    return null;
  }

  @Override
  public ResponseEntity<Authorization> getOrdersPaymentAuthorization(String id) {
    return null;
  }
}

