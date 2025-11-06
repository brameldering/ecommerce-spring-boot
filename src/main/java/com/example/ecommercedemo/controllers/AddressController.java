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
import org.springframework.web.bind.annotation.PathVariable;
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
  public ResponseEntity<Address> createAddress(AddAddressReq addAddressReq) {
    return service.createAddress(addAddressReq)
        .map(assembler::toModel) // add HATEOAS links
        .map(address -> status(HttpStatus.CREATED).body(address))
        .orElseThrow(() -> new AddressCreationException("Address creation failed"));
  }

  @Override
  public ResponseEntity<List<Address>> getAllAddresses () {
//    List<Address> addresses = service.getAllAddresses();
//    List<Address> addressesWithLinks = assembler.toModelList(addresses);
//    return ResponseEntity.ok(addressesWithLinks);

    return ResponseEntity.ok(Optional.ofNullable(service.getAllAddresses())
        .map(assembler::toModelList)
        .orElse(List.of()));
  }

  @Override
  public ResponseEntity<List<Address>> getCustomerAddresses (@PathVariable("id") UUID customerId) {
    return ResponseEntity.ok(
        service.getAddressesByCustomerId(customerId) // returns Optional<List<Address>>
            .map(assembler::toModelList)
            .orElse(List.of()) // If Optional is empty (service returned null), provide an empty List<Address>
    );
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
