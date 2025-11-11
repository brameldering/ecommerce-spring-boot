package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.AddressApi;
import com.example.ecommercedemo.exceptions.AddressCreationException;
import com.example.ecommercedemo.hateoas.AddressRepresentationModelAssembler;
import com.example.ecommercedemo.model.AddressReq;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.status;

@RestController
@Validated
@RequestMapping("/api/v1")
public class AddressController implements AddressApi {

  private final AddressService service;

  private final AddressRepresentationModelAssembler assembler;

  public AddressController(AddressService addressService, AddressRepresentationModelAssembler assembler) {
    this.service = addressService;
    this.assembler = assembler;
  }

  @Override
  public ResponseEntity<Address> createAddress(@PathVariable("id") UUID customerId, @Valid @RequestBody AddressReq addressReq) {
    // 1. Call the service method, which returns Address or throws an exception.
    Address createdAddress = service.createAddress(customerId, addressReq);

    // 2. Add HATEOAS links using the assembler.
    Address addressWithLinks = assembler.toModel(createdAddress);

    // 3. Return the 201 Created response.
    return status(HttpStatus.CREATED).body(addressWithLinks);
  }

  @Override
  public ResponseEntity<List<Address>> getAllAddresses () {
    return ResponseEntity.ok(Optional.ofNullable(service.getAllAddresses())
        .map(assembler::toModelList)
        .orElse(List.of()));
  }

  @Override
  public ResponseEntity<List<Address>> getCustomerAddresses (@PathVariable("id") UUID customerId) {
    return ResponseEntity.ok(assembler.toModelList(service.getAddressesByCustomerId(customerId)));
  }

  @Override
  public ResponseEntity<Address> getAddressById(UUID uuid) {
    return service.getAddressById(uuid)
        .map(assembler::toModel) // add HATEOAS links
        .map(ResponseEntity::ok).orElse(notFound().build());
  }

  @Override
  public ResponseEntity<Void> deleteAddressById(UUID uuid) {
    service.deleteAddressById(uuid);
    return accepted().build();
  }
}
