package com.example.ecommercedemo.address;

import com.example.ecommercedemo.model.AddressReq;
import com.example.ecommercedemo.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressService {
  Address createAddress(@NotNull(message = "Customer UUID cannot be null.") UUID customerId, @Valid AddressReq addressReq);
  List<Address> getAllAddresses();
  Optional<Address> getAddressById(@NotNull(message = "Address UUID cannot be null.") UUID AddressId);
  List<Address> getAddressesByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID customerId);
  void deleteAddressById(@NotNull(message = "Address UUID cannot be null.") UUID AddressId);
  }
