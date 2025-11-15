package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.CardEntity;
import com.example.ecommercedemo.entity.CustomerEntity;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.exceptions.ErrorCode;
import com.example.ecommercedemo.mappers.CardMapper;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.repository.CardRepository;
import com.example.ecommercedemo.repository.CustomerRepository;
import com.example.ecommercedemo.model.CardReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class CardServiceImpl implements CardService {

  private final CardRepository cardRepository;
  private final CustomerRepository customerRepository;
  private final CardMapper cardMapper;

  public CardServiceImpl(CardRepository cardRepository, CustomerRepository customerRepository, CardMapper cardMapper) {
    this.cardRepository = cardRepository;
    this.customerRepository = customerRepository;
    this.cardMapper = cardMapper;
  }

  @Override
  @Transactional
  public Card registerCard(UUID customerId, CardReq addCardReq) {
    // --- VALIDATION ---
    if (addCardReq == null) {
      throw new IllegalArgumentException("Card request cannot be null.");
    }
    if (customerId == null) {
      throw new IllegalArgumentException("CustomerId cannot be null.");
    }
    // --- END VALIDATION ---

      // Check if customerEntity exists
    CustomerEntity customerEntity = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));

    // UNNECESSARY LIMITATION:Check if a card already exists for this customerEntity
//    if (cardRepository.existsByCustomerId(customerId)) {
//      throw new CustomerAlreadyExistsException(ErrorCode.GENERIC_ALREADY_EXISTS);
//    }

    // Create and save new card
    CardEntity cardEntity = new CardEntity()
        .setCustomer(customerEntity)
        .setNumber(addCardReq.getCardNumber())
        .setCvv(addCardReq.getCvv())
        .setExpires(addCardReq.getExpires());

    CardEntity saved = cardRepository.save(cardEntity);
    return cardMapper.entityToModel(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Card> getAllCards() {
    return cardMapper.entityToModelList(cardRepository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<List<Card>> getCardsByCustomerId(UUID id) {
    return customerRepository.findById(id) // Returns Optional<CustomerEntity>
        .map(CustomerEntity::getCards) // Returns Optional<List<CardEntity>>
        .map(cardMapper::entityToModelList); // Returns Optional<List<Card>>
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Card> getCardById(UUID uuid) {
    return cardRepository.findById(uuid).map(cardMapper::entityToModel);
  }

  @Override
  @Transactional
  public void deleteCardById(UUID uuid) {
    cardRepository.deleteById(uuid);
  }
}
