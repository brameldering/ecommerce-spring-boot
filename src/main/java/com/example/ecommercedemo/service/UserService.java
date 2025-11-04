package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.model.User;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
  List<User> getAllCustomers();
  Optional<Card> getCardByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID id);
  Optional<User> getCustomerById(@NotNull(message = "Customer UUID cannot be null.") UUID id);
  void deleteCustomerById(@NotNull(message = "Customer UUID cannot be null.") UUID id);
}
