package com.example.ecommercedemo.controller;

import com.example.ecommercedemo.api.AddressApi;
import com.example.ecommercedemo.hateoas.AddressRepresentationModelAssembler;
import com.example.ecommercedemo.model.AddressReq;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.status;

@RestController
@Validated
@RequestMapping("/api/v1")
public class AddressController implements AddressApi {

  private final AddressService addressService;

  private final AddressRepresentationModelAssembler addressAssembler;

  public AddressController(AddressService addressService, AddressRepresentationModelAssembler addressAssembler) {
    this.addressService = addressService;
    this.addressAssembler = addressAssembler;
  }

  @Override
  public ResponseEntity<Address> createAddress(@PathVariable("id") UUID customerId, AddressReq addressReq) {
    // 1. Call the service method, which returns Address or throws an exception.
    Address createdAddress = addressService.createAddress(customerId, addressReq);

    // 2. Add HATEOAS links using the assembler.
    Address addressWithLinks = addressAssembler.toModel(createdAddress);

    // 3. Return the 201 Created response.
    return status(HttpStatus.CREATED).body(addressWithLinks);
  }

  @Override
  public ResponseEntity<List<Address>> getCustomerAddresses (@PathVariable("id") UUID id) {
    return ResponseEntity.ok(addressAssembler.toModelList(addressService.getAddressesByCustomerId(id)));
  }

  @Override
  public ResponseEntity<Address> getAddressById(UUID uuid) {
    return addressService.getAddressById(uuid)
        .map(addressAssembler::toModel) // add HATEOAS links
        .map(ResponseEntity::ok).orElse(notFound().build());
  }

  @Override
  public ResponseEntity<Void> deleteAddressById(UUID uuid) {
    addressService.deleteAddressById(uuid);
    return accepted().build();
  }
}
