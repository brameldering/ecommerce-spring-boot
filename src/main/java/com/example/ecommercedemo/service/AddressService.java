package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.AddAddressReq;
import com.example.ecommercedemo.model.Address;

import java.util.List;
import java.util.Optional;

public interface AddressService {
  Optional<Address> createAddress(AddAddressReq addAddressReq);
  void deleteAddressesById(String id);
  Optional<Address> getAddressesById(String id);
  List<Address> getAllAddresses();
}
