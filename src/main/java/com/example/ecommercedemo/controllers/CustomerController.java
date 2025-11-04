package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.CustomerApi;
import com.example.ecommercedemo.hateoas.UserRepresentationModelAssembler;
import com.example.ecommercedemo.model.User;
import com.example.ecommercedemo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
public class CustomerController implements CustomerApi {

  private final UserService service;

  private final UserRepresentationModelAssembler userAssembler;

  public CustomerController(UserService service, UserRepresentationModelAssembler userAssembler) {
    this.service = service;
    this.userAssembler = userAssembler;
  }

  @Override
  public ResponseEntity<List<User>> getAllCustomers() {
    return ok(service.getAllCustomers()
        .stream()
        .map(userAssembler::toModel)
        .toList());
  }

  @Override
  public ResponseEntity<User> getCustomerById(UUID uuid) {
     return service.getCustomerById(uuid) // 1. Returns Optional<User>
        .map(userAssembler::toModel) // 2. Applies HATEOAS links *inside* the Optional
        .map(ResponseEntity::ok)     // 3. Wraps the linked resource in a 200 OK
        .orElse(notFound().build()); // 4. Handles the empty case with a 404
  }

  @Override
  public ResponseEntity<Void> deleteCustomerById(UUID uuid) {
    service.deleteCustomerById(uuid);
    return accepted().build();
  }

}

