package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.CustomerApi;
import com.example.ecommercedemo.hateoas.AddressRepresentationModelAssembler;
import com.example.ecommercedemo.hateoas.CardRepresentationModelAssembler;
import com.example.ecommercedemo.hateoas.UserRepresentationModelAssembler;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.model.User;
import com.example.ecommercedemo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
public class CustomerController implements CustomerApi {

  private final UserService service;

  private final UserRepresentationModelAssembler userAssembler;
  private final AddressRepresentationModelAssembler addressAssembler;
  private final CardRepresentationModelAssembler cardAssembler;

  public CustomerController(UserService service, UserRepresentationModelAssembler userAssembler, AddressRepresentationModelAssembler addressAssembler, CardRepresentationModelAssembler cardAssembler) {
    this.service = service;
    this.userAssembler = userAssembler;
    this.addressAssembler = addressAssembler;
    this.cardAssembler = cardAssembler;
  }

  @Override
  public ResponseEntity<Void> deleteCustomerById(UUID uuid) {

//    UUID uuid = UUID.fromString(id);
    service.deleteCustomerById(uuid);
    return accepted().build();
  }

  @Override
  public ResponseEntity<List<Address>> getAddressesByCustomerId(UUID uuid) {

//    UUID uuid = UUID.fromString(id);
    return service.getAddressesByCustomerId(uuid)
        .map(addressAssembler::toModelList)
        .map(ResponseEntity::ok)
        .orElse(notFound().build());
  }

  @Override
  public ResponseEntity<List<User>> getAllCustomers() {
    return ok(service.getAllCustomers()
        .stream()
        .map(userAssembler::toModel)
        .toList());
  }

  @Override
  public ResponseEntity<Card> getCardByCustomerId(UUID uuid) {

//    UUID uuid = UUID.fromString(id);
    return service.getCardByCustomerId(uuid)
        .map(cardAssembler::toModel)
        .map(ResponseEntity::ok)
        .orElse(notFound().build());
  }

  @Override
  public ResponseEntity<User> getCustomerById(UUID uuid) {

//    UUID uuid = UUID.fromString(id);
    return service.getCustomerById(uuid) // 1. Returns Optional<User>
        .map(userAssembler::toModel) // 2. Applies HATEOAS links *inside* the Optional
        .map(ResponseEntity::ok)     // 3. Wraps the linked resource in a 200 OK
        .orElse(notFound().build()); // 4. Handles the empty case with a 404
  }
}

