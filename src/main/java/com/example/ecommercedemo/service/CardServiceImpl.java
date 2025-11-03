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
import com.example.ecommercedemo.model.AddCardReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class CardServiceImpl implements CardService {

  private final CardRepository repository;
  private final UserRepository userRepo;
  private final CardMapper mapper;

  public CardServiceImpl(CardRepository repository, UserRepository userRepo, CardMapper mapper) {
    this.repository = repository;
    this.userRepo = userRepo;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public void deleteCardById(UUID uuid) {
    repository.deleteById(uuid);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Card> getAllCards() {
    return mapper.entityToModelList(repository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Card> getCardById(UUID uuid) {
    return repository.findById(uuid).map(mapper::entityToModel);
  }

  @Override
  @Transactional
  public Optional<Card> registerCard(AddCardReq addCardReq) {
    UUID userId = addCardReq.getUserId();

    // Check if user exists
    UserEntity user = userRepo.findById(userId)
        .orElseThrow(() -> new CustomerNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));

    // Check if a card already exists for this user
    if (repository.existsByUserId(userId)) {
      throw new GenericAlreadyExistsException(ErrorCode.GENERIC_ALREADY_EXISTS);
    }

    // Create and save new card
    CardEntity cardEntity = new CardEntity()
        .setUser(user)
        .setNumber(addCardReq.getCardNumber())
        .setCvv(addCardReq.getCvv())
        .setExpires(addCardReq.getExpires());

    CardEntity saved = repository.save(cardEntity);
    return Optional.of(mapper.entityToModel(saved));
  }
}
