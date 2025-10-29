package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.CardEntity;
import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.exceptions.ErrorCode;
import com.example.ecommercedemo.exceptions.GenericAlreadyExistsException;
import com.example.ecommercedemo.hateoas.CardRepresentationModelAssembler;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.repository.CardRepository;
import com.example.ecommercedemo.repository.UserRepository;
import com.example.ecommercedemo.model.AddCardReq;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CardServiceImpl implements CardService {
  private final CardRepository repository;
  private final UserRepository userRepo;
  private final CardRepresentationModelAssembler assembler;

  public CardServiceImpl(CardRepository repository, UserRepository userRepo, CardRepresentationModelAssembler assembler) {
    this.repository = repository;
    this.userRepo = userRepo;
    this.assembler = assembler;
  }

  @Override
  @Transactional
  public void deleteCardById(String id) {
    repository.deleteById(UUID.fromString(id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Card> getAllCards() {
    return assembler.toListModel(repository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Card> getCardById(String id) {
    return repository.findById(UUID.fromString(id)).map(assembler::toModel);
  }

  @Override
  @Transactional
  public Optional<Card> registerCard(@Valid AddCardReq addCardReq) {
    UUID userId = UUID.fromString(addCardReq.getUserId());

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
    return Optional.of(assembler.toModel(saved));
  }


  private CardEntity toEntity(AddCardReq m) {
    CardEntity e = new CardEntity();
    Optional<UserEntity> user = userRepo.findById(UUID.fromString(m.getUserId()));
    user.ifPresent(e::setUser);
    return e.setNumber(m.getCardNumber()).setCvv(m.getCvv())
        .setExpires(m.getExpires());
  }
}
