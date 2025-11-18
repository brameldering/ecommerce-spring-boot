package com.example.ecommercedemo.admin;

import com.example.ecommercedemo.api.AdminApi;
import com.example.ecommercedemo.address.AddressRepresentationModelAssembler;
import com.example.ecommercedemo.card.CardRepresentationModelAssembler;
import com.example.ecommercedemo.customer.CustomerRepresentationModelAssembler;
import com.example.ecommercedemo.order.OrderRepresentationModelAssembler;
import com.example.ecommercedemo.model.*;
import com.example.ecommercedemo.address.AddressService;
import com.example.ecommercedemo.card.CardService;
import com.example.ecommercedemo.order.OrderService;
import com.example.ecommercedemo.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AdminController implements AdminApi {

  private final AddressService addressService;
  private final AddressRepresentationModelAssembler addressAssembler;
  private final CardService cardService;
  private final CardRepresentationModelAssembler cardAssembler;
  private final CustomerService customerService;
  private final CustomerRepresentationModelAssembler customerAssembler;
  private final OrderService orderService;
  private final OrderRepresentationModelAssembler orderAssembler;

//  public AdminController(AddressService addressService, AddressRepresentationModelAssembler addressAssembler, CardService cardService, CardRepresentationModelAssembler cardAssembler, CustomerService customerService, CustomerRepresentationModelAssembler customerAssembler, OrderService orderService, OrderRepresentationModelAssembler orderAssembler) {
//    this.addressService = addressService;
//    this.addressAssembler = addressAssembler;
//    this.cardService = cardService;
//    this.cardAssembler = cardAssembler;
//    this.customerService = customerService;
//    this.customerAssembler = customerAssembler;
//    this.orderService = orderService;
//    this.orderAssembler = orderAssembler;
//  }

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
    return ResponseEntity.ok(Optional.ofNullable(customerService.getAllCustomers())
        .orElse(List.of()) // Provide an empty list if null
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
