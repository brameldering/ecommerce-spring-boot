package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.Customer;
import com.example.ecommercedemo.model.CustomerReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
  Customer createUser(@Valid CustomerReq customerReq);
  Customer updateUser(@NotNull(message = "Customer UUID cannot be null.") UUID id, @Valid CustomerReq customerReq);
  List<Customer> getAllCustomers();
  Optional<Customer> getCustomerById(@NotNull(message = "Customer UUID cannot be null.") UUID id);
  void deleteCustomerById(@NotNull(message = "Customer UUID cannot be null.") UUID id);
}
