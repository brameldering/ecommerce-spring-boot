package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.CardApi;
import com.example.ecommercedemo.hateoas.CardRepresentationModelAssembler;
import com.example.ecommercedemo.model.AddCardReq;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.service.CardService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
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
  public ResponseEntity<Void> deleteCardById(UUID id) {
//    UUID uuid = UUID.fromString(id);
    service.deleteCardById(id);
    return accepted().build();
  }

  @Override
  public ResponseEntity<List<Card>> getAllCards() {
    List<Card> cards = service.getAllCards();
    // Add HATEOAS links to all cards in the list
    List<Card> cardsWithLinks = assembler.toModelList(cards);
    return ok(cardsWithLinks);
  }

  @Override
  public ResponseEntity<Card> getCardById(UUID uuid) {
//    UUID uuid = UUID.fromString(id);
    return service.getCardById(uuid)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElse(notFound().build());
  }

  @Override
  public ResponseEntity<Card> registerCard(AddCardReq addCardReq) {
    Card newCard = service.registerCard(addCardReq).get();
    // Add HATEOAS links to the newly created card
    Card cardWithLinks = assembler.toModel(newCard);
    return status(HttpStatus.CREATED).body(cardWithLinks);
  }
}
