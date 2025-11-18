package com.example.ecommercedemo.payment;

import com.example.ecommercedemo.api.PaymentApi;
import com.example.ecommercedemo.cart.CartController;
import com.example.ecommercedemo.model.Authorization;
import com.example.ecommercedemo.model.PaymentReq;
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

  private final PaymentService paymentService;
  private final PaymentRepresentationModelAssembler paymentAssembler;

  private static final Logger log = LoggerFactory.getLogger(CartController.class);

  public PaymentController(PaymentService paymentService, PaymentRepresentationModelAssembler paymentAssembler) {
    this.paymentService = paymentService;
    this.paymentAssembler = paymentAssembler;
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

