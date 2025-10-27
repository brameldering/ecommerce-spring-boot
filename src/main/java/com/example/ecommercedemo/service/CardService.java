package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.CardEntity;
import com.example.ecommercedemo.model.AddCardReq;
import jakarta.validation.Valid;

import java.util.Optional;

public interface CardService {
  void deleteCardById(String id);
  Iterable<CardEntity> getAllCards();
  Optional<CardEntity> getCardById(String id);
  Optional<CardEntity> registerCard(@Valid AddCardReq addCardReq);
}
