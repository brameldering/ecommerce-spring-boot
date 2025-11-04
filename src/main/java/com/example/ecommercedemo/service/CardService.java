package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.AddCardReq;
import com.example.ecommercedemo.model.Card;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardService {
  Optional<Card> registerCard(AddCardReq addCardReq);
  List<Card> getAllCards();
  Optional<Card> getCardById(@NotNull(message = "Card UUID cannot be null.") UUID uuid);
  Optional<List<Card>> getCardsByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID id);
  void deleteCardById(@NotNull(message = "Card UUID cannot be null.") UUID uuid);
}
