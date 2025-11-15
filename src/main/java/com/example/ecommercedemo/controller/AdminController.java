package com.example.ecommercedemo.controller;

import com.example.ecommercedemo.api.AdminApi;
import com.example.ecommercedemo.hateoas.AddressRepresentationModelAssembler;
import com.example.ecommercedemo.hateoas.CardRepresentationModelAssembler;
import com.example.ecommercedemo.hateoas.CustomerRepresentationModelAssembler;
import com.example.ecommercedemo.hateoas.OrderRepresentationModelAssembler;
import com.example.ecommercedemo.model.*;
import com.example.ecommercedemo.service.AddressService;
import com.example.ecommercedemo.service.CardService;
import com.example.ecommercedemo.service.OrderService;
import com.example.ecommercedemo.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.*;

@RestController
@Validated
@RequestMapping("/api/v1")
public class AdminController implements AdminApi {

  private final AddressService addressService;
  private final AddressRepresentationModelAssembler addressAssembler;
  private final CardService cardService;
  private final CardRepresentationModelAssembler cardAssembler;
  private final CustomerService customerService;
  private final CustomerRepresentationModelAssembler customerAssembler;
  private final OrderService orderService;
  private final OrderRepresentationModelAssembler orderAssembler;

  public AdminController(AddressService addressService, AddressRepresentationModelAssembler addressAssembler, CardService cardService, CardRepresentationModelAssembler cardAssembler, CustomerService customerService, CustomerRepresentationModelAssembler customerAssembler, OrderService orderService, OrderRepresentationModelAssembler orderAssembler) {
    this.addressService = addressService;
    this.addressAssembler = addressAssembler;
    this.cardService = cardService;
    this.cardAssembler = cardAssembler;
    this.customerService = customerService;
    this.customerAssembler = customerAssembler;
    this.orderService = orderService;
    this.orderAssembler = orderAssembler;
  }

  @Override
  public ResponseEntity<List<Address>> getAllAddresses () {
    return ResponseEntity.ok(Optional.ofNullable(addressService.getAllAddresses())
        .map(addressAssembler::toModelList)
        .orElse(List.of()));
  }

  @Override
  public ResponseEntity<List<Card>> getAllCards () {
    return ResponseEntity.ok(Optional.ofNullable(cardService.getAllCards())
        .map(cardAssembler::toModelList)
        .orElse(List.of()));
  }

  @Override
  public ResponseEntity<List<Customer>> getAllCustomers() {
    return ok(customerService.getAllCustomers()
        .stream()
        .map(customerAssembler::toModel)
        .toList());
  }

  @Override
  public ResponseEntity<List<Order>> getAllOrders () {
    return ResponseEntity.ok(Optional.ofNullable(orderService.getAllOrders())
        .map(orderAssembler::toModelList)
        .orElse(List.of()));
  }

}
