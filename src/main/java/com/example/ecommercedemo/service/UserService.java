package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.User;
import com.example.ecommercedemo.model.UserReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
  User createUser(@Valid UserReq userReq);
  User updateUser(@NotNull(message = "Customer UUID cannot be null.") UUID id, @Valid UserReq userReq);
  List<User> getAllCustomers();
  Optional<User> getCustomerById(@NotNull(message = "Customer UUID cannot be null.") UUID id);
  void deleteCustomerById(@NotNull(message = "Customer UUID cannot be null.") UUID id);
}
