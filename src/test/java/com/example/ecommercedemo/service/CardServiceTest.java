package com.example.ecommercedemo.service;

import com.example.ecommercedemo.card.CardServiceImpl;
import com.example.ecommercedemo.card.CardEntity;
import com.example.ecommercedemo.customer.CustomerEntity;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.card.CardMapper;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.model.CardReq;
import com.example.ecommercedemo.card.CardRepository;
import com.example.ecommercedemo.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

  @Mock
  private CardRepository cardRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private CardMapper cardMapper;

  @InjectMocks
  private CardServiceImpl cardService;

  // --- Test Data ---
  private UUID customerId;
  private UUID cardId;
  private CustomerEntity customerEntity;
  private CardEntity cardEntity;
  private Card cardModel;
  private CardReq cardReq;

  @BeforeEach
  void setUp() {
    customerId = UUID.randomUUID();
    cardId = UUID.randomUUID();

    // 1. Setup Entities
    customerEntity = new CustomerEntity();
    customerEntity.setId(customerId);

    cardEntity = new CardEntity()
        .setId(cardId)
        .setCustomer(customerEntity)
        .setNumber("1234567890123456")
        .setCvv("123")
        .setExpires("12/25");

    // 2. Setup Models/DTOs
    cardModel = new Card();
    cardModel.setId(cardId);
    cardModel.setCustomerId(customerId);
    cardModel.setCardNumber("************3456"); // Assuming mapper masks the number

    cardReq = new CardReq();
    cardReq.setCardNumber("1234567890123456");
    cardReq.setCvv("123");
    cardReq.setExpires("12/25");
  }

  // ------------------------------------------------------------------
  // registerCard Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("REGISTER: Should successfully register a new card")
  void registerCard_Success() {
    // --- Setup Mocks ---
    // 1. Find Customer
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
    // 2. Check if card exists (it doesn't)
//    when(cardRepository.existsByCustomerId(customerId)).thenReturn(false);
    // 3. Save the card (use any() because the entity is created *inside* the method)
    when(cardRepository.save(any(CardEntity.class))).thenReturn(cardEntity);
    // 4. Map the result
    when(cardMapper.entityToModel(cardEntity)).thenReturn(cardModel);

    // --- Execute ---
    Card result = cardService.registerCard(customerId, cardReq);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertEquals(cardModel.getId(), result.getId());
    assertEquals(cardModel.getCardNumber(), result.getCardNumber());

    verify(customerRepository, times(1)).findById(customerId);
//    verify(cardRepository, times(1)).existsByCustomerId(customerId);
    verify(cardRepository, times(1)).save(any(CardEntity.class));
  }

  // ------------------------------------------------------------------
  // Validation Tests for registerCard
  // ------------------------------------------------------------------

  @Test
  @DisplayName("REGISTER: Should throw IllegalArgumentException when CardReq is null")
  void registerCard_WhenRequestIsNull_ShouldThrowException() {
    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> cardService.registerCard(customerId, null)
    );
    assertEquals("Card request cannot be null.", exception.getMessage());
    verifyNoInteractions(customerRepository, cardRepository, cardMapper);
  }

  @Test
  @DisplayName("REGISTER: Should throw IllegalArgumentException when CustomerId is null")
  void registerCard_WhenCustomerIdIsNull_ShouldThrowException() {

    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> cardService.registerCard(null, cardReq)
    );
    assertEquals("CustomerId cannot be null.", exception.getMessage());
    verifyNoInteractions(customerRepository, cardRepository, cardMapper);
  }

  @Test
  @DisplayName("REGISTER: Should throw CustomerNotFoundException when Customer does not exist")
  void registerCard_WhenCustomerNotFound_ShouldThrowException() {
    // --- Setup Mocks ---
    when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

    // --- Execute & Assert ---
    assertThrows(
        CustomerNotFoundException.class,
        () -> cardService.registerCard(customerId, cardReq)
    );

    verify(customerRepository, times(1)).findById(customerId);
//    verify(cardRepository, never()).existsByCustomerId(any());
    verify(cardRepository, never()).save(any());
  }

  // ------------------------------------------------------------------
  // Query Methods Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should return Optional<List<Card>> when Customer has cards")
  void getCardsByCustomerId_WhenCustomerHasCards_ReturnsList() {
    // --- Setup Mocks ---
    List<CardEntity> entityList = List.of(cardEntity);
    List<Card> modelList = List.of(cardModel);

    // Set up the CustomerEntity to return the CardEntity list
    customerEntity.setCards(entityList);

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
    when(cardMapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    Optional<List<Card>> result = cardService.getCardsByCustomerId(customerId);

    // --- Assert & Verify ---
    assertTrue(result.isPresent());
    assertFalse(result.get().isEmpty());
    assertEquals(1, result.get().size());
    verify(customerRepository, times(1)).findById(customerId);
  }

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should return Optional.empty() when Customer is not found")
  void getCardsByCustomerId_WhenCustomerNotFound_ReturnsEmptyOptional() {
    // --- Setup Mocks ---
    when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

    // --- Execute ---
    Optional<List<Card>> result = cardService.getCardsByCustomerId(customerId);

    // --- Assert & Verify ---
    assertFalse(result.isPresent());
    verify(customerRepository, times(1)).findById(customerId);
    verify(cardMapper, never()).entityToModelList(any());
  }

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should return Optional<List<Card>> when Customer has no cards")
  void getCardsByCustomerId_WhenCustomerHasNoCards_ReturnsEmptyListInOptional() {
    // --- Setup Mocks ---
    customerEntity.setCards(Collections.emptyList()); // Customer exists but has empty list
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
    when(cardMapper.entityToModelList(Collections.emptyList())).thenReturn(Collections.emptyList());

    // --- Execute ---
    Optional<List<Card>> result = cardService.getCardsByCustomerId(customerId);

    // --- Assert & Verify ---
    assertTrue(result.isPresent());
    assertTrue(result.get().isEmpty());
  }

  @Test
  @DisplayName("GET_BY_ID: Should return Optional<Card> when found")
  void getCardById_WhenFound_ReturnsOptionalCard() {
    // --- Setup Mocks ---
    when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
    when(cardMapper.entityToModel(cardEntity)).thenReturn(cardModel);

    // --- Execute ---
    Optional<Card> result = cardService.getCardById(cardId);

    // --- Assert & Verify ---
    assertTrue(result.isPresent());
    assertEquals(cardId, result.get().getId());
  }

  @Test
  @DisplayName("GET_BY_ID: Should return Optional.empty() when not found")
  void getCardById_WhenNotFound_ReturnsEmptyOptional() {
    // --- Setup Mocks ---
    when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

    // --- Execute ---
    Optional<Card> result = cardService.getCardById(cardId);

    // --- Assert & Verify ---
    assertFalse(result.isPresent());
    verify(cardMapper, never()).entityToModel(any());
  }

  // ------------------------------------------------------------------
  // deleteCardById Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("DELETE: Should call repository existsById once")
  void deleteCardById_ShouldCallRepository() {
    // --- Execute ---
    cardService.deleteCardById(cardId);

    // --- Assert & Verify ---
    // Verify that deleteById was called exactly once with the correct ID
//    verify(cardRepository, times(1)).deleteById(cardId);
    verify(cardRepository, times(1)).existsById(cardId);
  }
}