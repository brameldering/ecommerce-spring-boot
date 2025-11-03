package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.AddAddressReq;
import com.example.ecommercedemo.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressService {
  Optional<Address> createAddress(@Valid AddAddressReq addAddressReq);
  void deleteAddressById(@NotNull(message = "Address UUID cannot be null.") UUID uuid);
  Optional<Address> getAddressById(@NotNull(message = "Address UUID cannot be null.") UUID uuid);
  List<Address> getAllAddresses();
}
