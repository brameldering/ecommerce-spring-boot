package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.CustomerApi;
import com.example.ecommercedemo.hateoas.UserRepresentationModelAssembler;
import com.example.ecommercedemo.model.User;
import com.example.ecommercedemo.model.UserReq;
import com.example.ecommercedemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.*;

@RestController
@Validated
@RequestMapping("/api/v1")
public class CustomerController implements CustomerApi {

  private final UserService service;

  private final UserRepresentationModelAssembler userAssembler;

  public CustomerController(UserService service, UserRepresentationModelAssembler userAssembler) {
    this.service = service;
    this.userAssembler = userAssembler;
  }

  @Override
  public ResponseEntity<User> createUser (UserReq userReq) {
    User createdUser = service.createUser(userReq);
    User userWithLinks = userAssembler.toModel(createdUser);
    return status(HttpStatus.CREATED).body(userWithLinks);
  }

  @Override
  public ResponseEntity<User> updateUser(@PathVariable("id") UUID id, @Valid @RequestBody UserReq userReq) {

    User updatedUser = service.updateUser(id, userReq);
    User userWithLinks = userAssembler.toModel(updatedUser);
    return status(HttpStatus.OK).body(userWithLinks);
  }

  @Override
  public ResponseEntity<User> getCustomerById(UUID id) {
     return service.getCustomerById(id) // 1. Returns Optional<User>
        .map(userAssembler::toModel) // 2. Applies HATEOAS links *inside* the Optional
        .map(ResponseEntity::ok)     // 3. Wraps the linked resource in a 200 OK
        .orElse(notFound().build()); // 4. Handles the empty case with a 404
  }

  @Override
  public ResponseEntity<Void> deleteCustomerById(UUID id) {
    service.deleteCustomerById(id);
    return accepted().build();
  }
}

