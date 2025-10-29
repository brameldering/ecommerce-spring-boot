package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.CardApi;
import com.example.ecommercedemo.model.AddCardReq;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
public class CardController implements CardApi {

  private final CardService service;

  public CardController(CardService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<Void> deleteCardById(String id) {
    service.deleteCardById(id);
    return accepted().build();
  }

  @Override
  public ResponseEntity<List<Card>> getAllCards() {
    return ok(service.getAllCards());
  }

  @Override
  public ResponseEntity<Card> getCardById(String id) {
    return service.getCardById(id).map(ResponseEntity::ok)
        .orElse(notFound().build());
  }

  @Override
  public ResponseEntity<Card> registerCard(AddCardReq addCardReq) {
    return status(HttpStatus.CREATED).body(service.registerCard(addCardReq).get());
  }
}
