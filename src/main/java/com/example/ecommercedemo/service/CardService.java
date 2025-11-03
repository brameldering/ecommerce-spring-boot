package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.AddCardReq;
import com.example.ecommercedemo.model.Card;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardService {
  void deleteCardById(@NotNull(message = "Card UUID cannot be null.") UUID uuid);
  List<Card> getAllCards();
  Optional<Card> getCardById(@NotNull(message = "Card UUID cannot be null.") UUID uuid);
  Optional<Card> registerCard(AddCardReq addCardReq);
}
