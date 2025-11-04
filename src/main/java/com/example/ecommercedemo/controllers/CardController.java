package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.CardApi;
import com.example.ecommercedemo.hateoas.CardRepresentationModelAssembler;
import com.example.ecommercedemo.model.AddCardReq;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.service.CardService;

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
public class CardController implements CardApi {

  private final CardService service;

  private final CardRepresentationModelAssembler assembler;

  public CardController(CardService service, CardRepresentationModelAssembler assembler) {
    this.service = service;
    this.assembler = assembler;
  }

  @Override
  public ResponseEntity<Card> registerCard(AddCardReq addCardReq) {
    Card newCard = service.registerCard(addCardReq).get();
    // Add HATEOAS links to the newly created card
    Card cardWithLinks = assembler.toModel(newCard);
    return status(HttpStatus.CREATED).body(cardWithLinks);
  }

  @Override
  public ResponseEntity<List<Card>> getCards(UUID customerId) {
    List<Card> cards;

    // Check if the optional query parameter 'customerId' is provided (is not null)
    if (Objects.nonNull(customerId)) {
      // get cards for customer
      cards = service.getCardsByCustomerId(customerId).orElse(List.of());
    } else {
      // CustomerId is null -> get all cards
      cards = service.getAllCards();
    }
    // Add HATEOAS links to all cards in the list
    List<Card> cardsWithLinks = assembler.toModelList(cards);
    return ResponseEntity.ok(cardsWithLinks);
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
