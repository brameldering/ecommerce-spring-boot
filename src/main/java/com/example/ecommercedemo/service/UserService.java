package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
  List<User> getAllCustomers();
  Optional<List<Address>> getAddressesByCustomerId(String id);
  Optional<Card> getCardByCustomerId(String id);
  Optional<User> getCustomerById(String id);
  void deleteCustomerById(String id);
}
