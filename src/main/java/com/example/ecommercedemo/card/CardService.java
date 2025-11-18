package com.example.ecommercedemo.card;

import com.example.ecommercedemo.model.CardReq;
import com.example.ecommercedemo.model.Card;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardService {
  Card registerCard(@NotNull(message = "Customer UUID cannot be null.") UUID id, @Valid CardReq cardReq);
  List<Card> getAllCards();
  Optional<Card> getCardById(@NotNull(message = "Card UUID cannot be null.") UUID uuid);
  Optional<List<Card>> getCardsByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID id);
  boolean deleteCardById(@NotNull(message = "Card UUID cannot be null.") UUID uuid);
}
