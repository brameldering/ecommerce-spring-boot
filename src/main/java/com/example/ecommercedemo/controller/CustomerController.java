package com.example.ecommercedemo.controller;

import com.example.ecommercedemo.api.CustomerApi;
import com.example.ecommercedemo.hateoas.CustomerRepresentationModelAssembler;
import com.example.ecommercedemo.model.Customer;
import com.example.ecommercedemo.model.CustomerReq;
import com.example.ecommercedemo.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.*;

@RestController
@Validated
@RequestMapping("/api/v1")
public class CustomerController implements CustomerApi {

  private final CustomerService service;

  private final CustomerRepresentationModelAssembler userAssembler;

  public CustomerController(CustomerService service, CustomerRepresentationModelAssembler userAssembler) {
    this.service = service;
    this.userAssembler = userAssembler;
  }

  @Override
  public ResponseEntity<Customer> createCustomer (CustomerReq customerReq) {
    Customer createdCustomer = service.createUser(customerReq);
    Customer customerWithLinks = userAssembler.toModel(createdCustomer);
    return status(HttpStatus.CREATED).body(customerWithLinks);
  }

  @Override
  public ResponseEntity<Customer> updateCustomer(@PathVariable("id") UUID id, CustomerReq customerReq) {

    Customer updatedCustomer = service.updateUser(id, customerReq);
    Customer userWithLinks = userAssembler.toModel(updatedCustomer);
    return status(HttpStatus.OK).body(userWithLinks);
  }

  @Override
  public ResponseEntity<Customer> getCustomerById(UUID id) {
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

