package com.example.ecommercedemo.customer;

import com.example.ecommercedemo.api.CustomerApi;
import com.example.ecommercedemo.model.Customer;
import com.example.ecommercedemo.model.CustomerReq;
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

  private final CustomerService customerService;

  private final CustomerRepresentationModelAssembler customerAssembler;

  public CustomerController(CustomerService customerService, CustomerRepresentationModelAssembler customerAssembler) {
    this.customerService = customerService;
    this.customerAssembler = customerAssembler;
  }

  @Override
  public ResponseEntity<Customer> createCustomer (CustomerReq customerReq) {
    Customer createdCustomer = customerService.createCustomer(customerReq);
    Customer customerWithLinks = customerAssembler.toModel(createdCustomer);
    return status(HttpStatus.CREATED).body(customerWithLinks);
  }

  @Override
  public ResponseEntity<Customer> updateCustomer(@PathVariable("id") UUID id, CustomerReq customerReq) {

    Customer updatedCustomer = customerService.updateCustomer(id, customerReq);
    Customer customerWithLinks = customerAssembler.toModel(updatedCustomer);
    return status(HttpStatus.OK).body(customerWithLinks);
  }

  @Override
  public ResponseEntity<Customer> getCustomerById(UUID id) {
     return customerService.getCustomerById(id) // 1. Returns Optional<Customer>
        .map(customerAssembler::toModel) // 2. Applies HATEOAS links *inside* the Optional
        .map(ResponseEntity::ok)     // 3. Wraps the linked resource in a 200 OK
        .orElse(notFound().build()); // 4. Handles the empty case with a 404
  }

  @Override
  public ResponseEntity<Void> deleteCustomerById(UUID id) {
    customerService.deleteCustomerById(id);
    return accepted().build();
  }
}

