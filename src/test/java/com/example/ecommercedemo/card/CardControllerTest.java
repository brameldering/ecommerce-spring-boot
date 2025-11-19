package com.example.ecommercedemo.card;

import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.model.CardReq;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
public class CardControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CardService cardService;

  // Mock HATEOAS Assembler: returns the object as is for simple testing
  @MockBean
  private CardRepresentationModelAssembler cardAssembler;

  private final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private final UUID CARD_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

  private CardReq cardReq;
  private Card mockCard;
  private List<Card> mockCardList;

  @BeforeEach
  void setUp() {
    // Setup mock data
    cardReq = new CardReq()
        .cardNumber("4111222233334444")
        .expires("12/25")
        .cvv("123");

    mockCard = new Card()
        .id(CARD_ID)
        .customerId(CUSTOMER_ID)
        .cardNumber("************4444") // Masked
        .expires("12/25");

    mockCardList = List.of(mockCard);

    // Mock the assembler to return the input Card object(s)
    when(cardAssembler.toModel(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(cardAssembler.toModelList(any())).thenAnswer(invocation -> invocation.getArgument(0));
  }

  // --- 1. POST /customers/{id}/cards (Register Card) ---
  @Test
  void registerCard_shouldReturn201Created_andCard() throws Exception {
    // Arrange
    when(cardService.registerCard(eq(CUSTOMER_ID), any(CardReq.class))).thenReturn(mockCard);

    // Act & Assert
    mockMvc.perform(post("/api/v1/customers/{id}/cards", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cardReq)))
        .andExpect(status().isCreated()) // Expect 201 CREATED
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(CARD_ID.toString()))
        .andExpect(jsonPath("$.expires").value("12/25"));

    // Verify service method was called
    verify(cardService, times(1)).registerCard(eq(CUSTOMER_ID), any(CardReq.class));
  }

  // --- 2. GET /customers/{id}/cards (Get Customer Cards) ---
  @Test
  void getCustomerCards_shouldReturn200Ok_andListOfCards() throws Exception {
    // Arrange
    when(cardService.getCardsByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(mockCardList));

    // Act & Assert
    mockMvc.perform(get("/api/v1/customers/{id}/cards", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // Expect 200 OK
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(CARD_ID.toString()));

    verify(cardService, times(1)).getCardsByCustomerId(CUSTOMER_ID);
  }

  @Test
  void getCustomerCards_shouldReturn200Ok_andEmptyList_whenNoCardsFound() throws Exception {
    // Arrange
    // Service returns Optional.empty(), leading to orElse(List.of()) in controller
    when(cardService.getCardsByCustomerId(CUSTOMER_ID)).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(get("/api/v1/customers/{id}/cards", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // Expect 200 OK
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));

    verify(cardService, times(1)).getCardsByCustomerId(CUSTOMER_ID);
  }

  // --- 3. GET /cards/{id} (Get Card by ID) ---
  @Test
  void getCardById_shouldReturn200Ok_whenCardExists() throws Exception {
    // Arrange
    when(cardService.getCardById(CARD_ID)).thenReturn(Optional.of(mockCard));

    // Act & Assert
    mockMvc.perform(get("/api/v1/cards/{id}", CARD_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // Expect 200 OK
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(CARD_ID.toString()));

    verify(cardService, times(1)).getCardById(CARD_ID);
  }

  @Test
  void getCardById_shouldReturn404NotFound_whenCardDoesNotExist() throws Exception {
    // Arrange
    when(cardService.getCardById(CARD_ID)).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(get("/api/v1/cards/{id}", CARD_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound()) // Expect 404 NOT FOUND
        .andExpect(content().string(""));

    verify(cardService, times(1)).getCardById(CARD_ID);
  }

  // --- 4. DELETE /cards/{id} (Delete Card) ---
  @Test
  void deleteCardById_shouldReturn204NoContent_whenCardIsDeleted() throws Exception {
    // Arrange
    when(cardService.deleteCardById(CARD_ID)).thenReturn(true); // Mock service returns true (deleted)

    // Act & Assert
    mockMvc.perform(delete("/api/v1/cards/{id}", CARD_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent()) // Expect 204 NO CONTENT
        .andExpect(content().string(""));

    verify(cardService, times(1)).deleteCardById(CARD_ID);
  }

  @Test
  void deleteCardById_shouldReturn404NotFound_whenCardDoesNotExist() throws Exception {
    // Arrange
    when(cardService.deleteCardById(CARD_ID)).thenReturn(false); // Mock service returns false (not found)

    // Act & Assert
    mockMvc.perform(delete("/api/v1/cards/{id}", CARD_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound()) // Expect 404 NOT FOUND
        .andExpect(content().string(""));

    verify(cardService, times(1)).deleteCardById(CARD_ID);
  }
}