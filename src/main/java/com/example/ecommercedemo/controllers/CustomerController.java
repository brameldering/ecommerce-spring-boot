package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.CustomerApi;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.model.User;
import com.example.ecommercedemo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class CustomerController implements CustomerApi {

  private final UserService service;

  public CustomerController(UserService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<Void> deleteCustomerById(String id) {
    service.deleteCustomerById(id);
    return accepted().build();
  }

  @Override
  public ResponseEntity<List<Address>> getAddressesByCustomerId(String id) {
    return service.getAddressesByCustomerId(id)
        .map(ResponseEntity::ok)
        .orElse(notFound().build());
  }

  @Override
  public ResponseEntity<List<User>> getAllCustomers() {
    return ok(service.getAllCustomers());
  }

  @Override
  public ResponseEntity<Card> getCardByCustomerId(String id) {
    return service.getCardByCustomerId(id)
        .map(ResponseEntity::ok)
        .orElse(notFound().build());
  }

  @Override
  public ResponseEntity<User> getCustomerById(String id) {
    return service.getCustomerById(id)
        .map(ResponseEntity::ok)
        .orElse(notFound().build());
  }
}

