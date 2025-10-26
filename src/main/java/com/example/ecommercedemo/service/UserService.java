package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.AddressEntity;
import com.example.ecommercedemo.entity.CardEntity;
import com.example.ecommercedemo.entity.UserEntity;

import java.util.Optional;

public interface UserService {
  Iterable<UserEntity> getAllCustomers();
  Optional<Iterable<AddressEntity>> getAddressesByCustomerId(String id);
  Optional<CardEntity> getCardByCustomerId(String id);
  Optional<UserEntity> getCustomerById(String id);
  void deleteCustomerById(String id);
}
