package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.CardEntity;
import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.exceptions.GenericAlreadyExistsException;
import com.example.ecommercedemo.mappers.CardMapper;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.model.CardReq;
import com.example.ecommercedemo.repository.CardRepository;
import com.example.ecommercedemo.repository.UserRepository;
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
  private UserRepository userRepository;

  @Mock
  private CardMapper mapper;

  @InjectMocks
  private CardServiceImpl cardService;

  // --- Test Data ---
  private UUID customerId;
  private UUID cardId;
  private UserEntity userEntity;
  private CardEntity cardEntity;
  private Card cardModel;
  private CardReq cardReq;

  @BeforeEach
  void setUp() {
    customerId = UUID.randomUUID();
    cardId = UUID.randomUUID();

    // 1. Setup Entities
    userEntity = new UserEntity();
    userEntity.setId(customerId);

    cardEntity = new CardEntity()
        .setId(cardId)
        .setUser(userEntity)
        .setNumber("1234567890123456")
        .setCvv("123")
        .setExpires("12/25");

    // 2. Setup Models/DTOs
    cardModel = new Card();
    cardModel.setId(cardId);
    cardModel.setUserId(customerId);
    cardModel.setCardNumber("************3456"); // Assuming mapper masks the number

    cardReq = new CardReq();
    cardReq.setUserId(customerId);
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
    // 1. Find user
    when(userRepository.findById(customerId)).thenReturn(Optional.of(userEntity));
    // 2. Check if card exists (it doesn't)
    when(cardRepository.existsByUserId(customerId)).thenReturn(false);
    // 3. Save the card (use any() because the entity is created *inside* the method)
    when(cardRepository.save(any(CardEntity.class))).thenReturn(cardEntity);
    // 4. Map the result
    when(mapper.entityToModel(cardEntity)).thenReturn(cardModel);

    // --- Execute ---
    Card result = cardService.registerCard(cardReq);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertEquals(cardModel.getId(), result.getId());
    assertEquals(cardModel.getCardNumber(), result.getCardNumber());

    verify(userRepository, times(1)).findById(customerId);
    verify(cardRepository, times(1)).existsByUserId(customerId);
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
        () -> cardService.registerCard(null)
    );
    assertEquals("Card request cannot be null.", exception.getMessage());
    verifyNoInteractions(userRepository, cardRepository, mapper);
  }

  @Test
  @DisplayName("REGISTER: Should throw IllegalArgumentException when UserId is null")
  void registerCard_WhenUserIdIsNull_ShouldThrowException() {
    // --- Setup ---
    cardReq.setUserId(null);

    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> cardService.registerCard(cardReq)
    );
    assertEquals("UserId cannot be null.", exception.getMessage());
    verifyNoInteractions(userRepository, cardRepository, mapper);
  }

  @Test
  @DisplayName("REGISTER: Should throw CustomerNotFoundException when user does not exist")
  void registerCard_WhenUserNotFound_ShouldThrowException() {
    // --- Setup Mocks ---
    when(userRepository.findById(customerId)).thenReturn(Optional.empty());

    // --- Execute & Assert ---
    assertThrows(
        CustomerNotFoundException.class,
        () -> cardService.registerCard(cardReq)
    );

    verify(userRepository, times(1)).findById(customerId);
    verify(cardRepository, never()).existsByUserId(any());
    verify(cardRepository, never()).save(any());
  }

  @Test
  @DisplayName("REGISTER: Should throw GenericAlreadyExistsException when card already exists")
  void registerCard_WhenCardAlreadyExists_ShouldThrowException() {
    // --- Setup Mocks ---
    when(userRepository.findById(customerId)).thenReturn(Optional.of(userEntity));
    when(cardRepository.existsByUserId(customerId)).thenReturn(true);

    // --- Execute & Assert ---
    assertThrows(
        GenericAlreadyExistsException.class,
        () -> cardService.registerCard(cardReq)
    );

    verify(userRepository, times(1)).findById(customerId);
    verify(cardRepository, times(1)).existsByUserId(customerId);
    verify(cardRepository, never()).save(any());
  }

  // ------------------------------------------------------------------
  // Query Methods Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should return Optional<List<Card>> when user has cards")
  void getCardsByCustomerId_WhenUserHasCards_ReturnsList() {
    // --- Setup Mocks ---
    List<CardEntity> entityList = List.of(cardEntity);
    List<Card> modelList = List.of(cardModel);

    // Set up the UserEntity to return the CardEntity list
    userEntity.setCards(entityList);

    when(userRepository.findById(customerId)).thenReturn(Optional.of(userEntity));
    when(mapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    Optional<List<Card>> result = cardService.getCardsByCustomerId(customerId);

    // --- Assert & Verify ---
    assertTrue(result.isPresent());
    assertFalse(result.get().isEmpty());
    assertEquals(1, result.get().size());
    verify(userRepository, times(1)).findById(customerId);
  }

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should return Optional.empty() when user is not found")
  void getCardsByCustomerId_WhenUserNotFound_ReturnsEmptyOptional() {
    // --- Setup Mocks ---
    when(userRepository.findById(customerId)).thenReturn(Optional.empty());

    // --- Execute ---
    Optional<List<Card>> result = cardService.getCardsByCustomerId(customerId);

    // --- Assert & Verify ---
    assertFalse(result.isPresent());
    verify(userRepository, times(1)).findById(customerId);
    verify(mapper, never()).entityToModelList(any());
  }

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should return Optional<List<Card>> when user has no cards")
  void getCardsByCustomerId_WhenUserHasNoCards_ReturnsEmptyListInOptional() {
    // --- Setup Mocks ---
    userEntity.setCards(Collections.emptyList()); // User exists but has empty list
    when(userRepository.findById(customerId)).thenReturn(Optional.of(userEntity));
    when(mapper.entityToModelList(Collections.emptyList())).thenReturn(Collections.emptyList());

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
    when(mapper.entityToModel(cardEntity)).thenReturn(cardModel);

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
    verify(mapper, never()).entityToModel(any());
  }

  // ------------------------------------------------------------------
  // deleteCardById Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("DELETE: Should call repository deleteById once")
  void deleteCardById_ShouldCallRepository() {
    // --- Execute ---
    cardService.deleteCardById(cardId);

    // --- Assert & Verify ---
    // Verify that deleteById was called exactly once with the correct ID
    verify(cardRepository, times(1)).deleteById(cardId);
  }
}