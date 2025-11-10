package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.CardApi;
import com.example.ecommercedemo.hateoas.CardRepresentationModelAssembler;
import com.example.ecommercedemo.model.CardReq;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.service.CardService;

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
public class CardController implements CardApi {

  private final CardService service;

  private final CardRepresentationModelAssembler assembler;

  public CardController(CardService service, CardRepresentationModelAssembler assembler) {
    this.service = service;
    this.assembler = assembler;
  }

  @Override
  public ResponseEntity<Card> registerCard(@Valid @RequestBody CardReq cardReq) {
    Card newCard = service.registerCard(cardReq).get();
    // Add HATEOAS links to the newly created card
    Card cardWithLinks = assembler.toModel(newCard);
    return status(HttpStatus.CREATED).body(cardWithLinks);
  }

  @Override
  public ResponseEntity<List<Card>> getAllCards () {
    return ResponseEntity.ok(Optional.ofNullable(service.getAllCards())
        .map(assembler::toModelList)
        .orElse(List.of()));
  }

  @Override
  public ResponseEntity<List<Card>> getCustomerCards (@PathVariable("id") UUID customerId) {
    return ResponseEntity.ok(
        service.getCardsByCustomerId(customerId) // returns Optional<List<Card>>
            .map(assembler::toModelList)
            .orElse(List.of()) // If Optional is empty (service returned null), provide an empty List<Card>
    );
  }

  @Override
  public ResponseEntity<Card> getCardById(UUID uuid) {
    return service.getCardById(uuid)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElse(notFound().build());
  }

  @Override
  public ResponseEntity<Void> deleteCardById(UUID id) {
    service.deleteCardById(id);
    return accepted().build();
  }
}
