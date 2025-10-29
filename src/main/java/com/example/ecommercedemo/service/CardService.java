package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.AddCardReq;
import com.example.ecommercedemo.model.Card;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface CardService {
  void deleteCardById(String id);
  List<Card> getAllCards();
  Optional<Card> getCardById(String id);
  Optional<Card> registerCard(@Valid AddCardReq addCardReq);
}
