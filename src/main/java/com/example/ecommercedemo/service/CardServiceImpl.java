package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.CardEntity;
import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.exceptions.ErrorCode;
import com.example.ecommercedemo.exceptions.GenericAlreadyExistsException;
import com.example.ecommercedemo.mappers.CardMapper;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.repository.CardRepository;
import com.example.ecommercedemo.repository.UserRepository;
import com.example.ecommercedemo.model.CardReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class CardServiceImpl implements CardService {

  private final CardRepository cardRepository;
  private final UserRepository userRepository;
  private final CardMapper mapper;

  public CardServiceImpl(CardRepository cardRepository, UserRepository userRepository, CardMapper mapper) {
    this.cardRepository = cardRepository;
    this.userRepository = userRepository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public Card registerCard(CardReq addCardReq) {
    // --- VALIDATION ---
    if (Objects.isNull(addCardReq)) {
      throw new IllegalArgumentException("Card request cannot be null.");
    }
    if (Objects.isNull(addCardReq.getUserId())) {
      throw new IllegalArgumentException("UserId cannot be null.");
    }
    // --- END VALIDATION ---

    UUID userId = addCardReq.getUserId();

    // Check if user exists
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomerNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));

    // Check if a card already exists for this user
    if (cardRepository.existsByUserId(userId)) {
      throw new GenericAlreadyExistsException(ErrorCode.GENERIC_ALREADY_EXISTS);
    }

    // Create and save new card
    CardEntity cardEntity = new CardEntity()
        .setUser(user)
        .setNumber(addCardReq.getCardNumber())
        .setCvv(addCardReq.getCvv())
        .setExpires(addCardReq.getExpires());

    CardEntity saved = cardRepository.save(cardEntity);
    return mapper.entityToModel(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Card> getAllCards() {
    return mapper.entityToModelList(cardRepository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<List<Card>> getCardsByCustomerId(UUID id) {
    return userRepository.findById(id) // Returns Optional<UserEntity>
        .map(UserEntity::getCards) // Returns Optional<List<CardEntity>>
        .map(mapper::entityToModelList); // Returns Optional<List<Card>>
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Card> getCardById(UUID uuid) {
    return cardRepository.findById(uuid).map(mapper::entityToModel);
  }

  @Override
  @Transactional
  public void deleteCardById(UUID uuid) {
    cardRepository.deleteById(uuid);
  }
}
