package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.AddressEntity;
import com.example.model.AddAddressReq;

import java.util.Optional;

public interface AddressService {
  Optional<AddressEntity> createAddress(AddAddressReq addAddressReq);
  void deleteAddressesById(String id);
  Optional<AddressEntity> getAddressesById(String id);
  Iterable<AddressEntity> getAllAddresses();
}
