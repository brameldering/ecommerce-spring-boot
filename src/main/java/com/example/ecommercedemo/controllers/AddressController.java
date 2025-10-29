package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.AddressApi;
import com.example.ecommercedemo.exceptions.AddressCreationException;
import com.example.ecommercedemo.model.AddAddressReq;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.status;

@RestController
public class AddressController implements AddressApi {

  private final AddressService service;

  public AddressController(AddressService addressService) {
    this.service = addressService;
  }

  @Override
  public ResponseEntity<Address> createAddress(@Valid AddAddressReq addAddressReq) {
    return service.createAddress(addAddressReq)
        .map(address -> status(HttpStatus.CREATED).body(address))
        .orElseThrow(() -> new AddressCreationException("Address creation failed"));
  }

  @Override
  public ResponseEntity<Void> deleteAddressesById(String id) {
    service.deleteAddressesById(id);
    return accepted().build();
  }

  @Override
  public ResponseEntity<Address> getAddressesById(String id) {
    return service.getAddressesById(id)
        .map(ResponseEntity::ok).orElse(notFound().build());
  }

  @Override
  public ResponseEntity<List<Address>> getAllAddresses() {
    return ResponseEntity.ok(service.getAllAddresses());
  }
}
