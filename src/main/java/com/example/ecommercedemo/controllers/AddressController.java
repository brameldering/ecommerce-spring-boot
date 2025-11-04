package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.AddressApi;
import com.example.ecommercedemo.exceptions.AddressCreationException;
import com.example.ecommercedemo.hateoas.AddressRepresentationModelAssembler;
import com.example.ecommercedemo.model.AddAddressReq;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.status;

@RestController
@Validated
public class AddressController implements AddressApi {

  private final AddressService service;

  private final AddressRepresentationModelAssembler assembler;

  public AddressController(AddressService addressService, AddressRepresentationModelAssembler assembler) {
    this.service = addressService;
    this.assembler = assembler;
  }

  @Override
  public ResponseEntity<Address> createAddress(AddAddressReq addAddressReq) {
    return service.createAddress(addAddressReq)
        .map(assembler::toModel) // add HATEOAS links
        .map(address -> status(HttpStatus.CREATED).body(address))
        .orElseThrow(() -> new AddressCreationException("Address creation failed"));
  }

  @Override
  public ResponseEntity<List<Address>> getAddresses(UUID customerId) {
    List<Address> addresses;

    // Check if the optional query parameter 'customerId' is provided (is not null)
    if (Objects.nonNull(customerId)) {
      // If customerId is provided, filter by customer
      addresses = service.getAddressesByCustomerId(customerId).orElse(List.of());
    } else {
      // If customerId is null, return all addresses
      addresses = service.getAllAddresses();
    }

    // Apply HATEOAS links and return
    List<Address> addressesWithLinks = assembler.toModelList(addresses);
    return ResponseEntity.ok(addressesWithLinks);
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
