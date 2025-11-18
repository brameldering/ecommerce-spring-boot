package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.CardEntity;
import com.example.ecommercedemo.entity.CustomerEntity;
import com.example.ecommercedemo.exceptions.CardAlreadyExistsException;
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

  // Regex for MM/YY format
  private static final String EXPIRY_PATTERN = "^(0[1-9]|1[0-2])\\/\\d{2}$";

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
    // Check if customerEntity exists
    CustomerEntity customerEntity = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));

    // Check if a card with the provided number already exists for this customerEntity
    if (cardRepository.existsByCustomerIdAndNumber(customerId, addCardReq.getCardNumber())) {
      throw new CardAlreadyExistsException(ErrorCode.CARD_ALREADY_EXISTS);
    }

    // Check for strict MM/YY format
    String expiry = addCardReq.getExpires();
    if (expiry == null || !expiry.matches(EXPIRY_PATTERN)) {
      throw new IllegalArgumentException("Card expiration date '" + expiry + "' must be in the strict MM/YY format.");
    }

    // --- END VALIDATION ---

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
  public boolean deleteCardById(UUID uuid) {
    if (uuid == null) {
      // Optional: Handle null input gracefully, though controller should prevent this
      return false;
    }

    // 1. Check if the entity exists
    boolean exists = cardRepository.existsById(uuid);

    if (exists) {
      // 2. If it exists, perform the deletion
      cardRepository.deleteById(uuid);

      // Note: For systems demanding absolute certainty, you could check again
      // after deletion, but for a simple primary key delete, this is sufficient.

      return true; // Card was found and deleted
    } else {
      // 3. If it doesn't exist, return false
      return false; // Card was not found
    }
  }
}
