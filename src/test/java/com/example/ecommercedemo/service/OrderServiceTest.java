package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.mappers.OrderMapper;
import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.model.OrderReq;
import com.example.ecommercedemo.repository.OrderRepository;
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
class OrderServiceTest {

  @Mock
  private OrderRepository repository;

  @Mock
  private OrderMapper mapper;

  @InjectMocks
  private OrderServiceImpl orderService;

  // --- Test Data ---
  private UUID orderId;
  private UUID customerId;
  private UUID addressId;
  private UUID cardId;
  private OrderEntity orderEntity;
  private Order orderModel;
  private OrderReq orderReq;

  @BeforeEach
  void setUp() {
    orderId = UUID.randomUUID();
    customerId = UUID.randomUUID();
    addressId = UUID.randomUUID();
    cardId = UUID.randomUUID();

    // 1. Setup Entities
    orderEntity = new OrderEntity();
    orderEntity.setId(orderId);
    // Set other properties as needed for a real entity

    // 2. Setup Models/DTOs
    orderModel = new Order();
    orderModel.setId(orderId);
    // Set other properties as needed for a real model

    orderReq = new OrderReq();
    orderReq.setCustomerId(customerId);
    orderReq.setAddressId(addressId);
    orderReq.setCardId(cardId);
    // Set other properties as needed for a real request
  }

  // ------------------------------------------------------------------
  // addOrder Tests (Creation)
  // ------------------------------------------------------------------

  @Test
  @DisplayName("ADD: Should successfully create and return a new Order")
  void addOrder_Success() {
    // --- Setup Mocks ---
    when(repository.insert(orderReq)).thenReturn(orderEntity);
    when(mapper.entityToModel(orderEntity)).thenReturn(orderModel);

    // --- Execute ---
    Order result = orderService.addOrder(orderReq);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertEquals(orderModel.getId(), result.getId());
    verify(repository, times(1)).insert(orderReq);
  }

  // ------------------------------------------------------------------
  // Validation Tests for addOrder (IllegalArgumentException)
  // ------------------------------------------------------------------

  @Test
  @DisplayName("ADD: Should throw IllegalArgumentException when OrderReq is null")
  void addOrder_WhenOrderReqIsNull_ShouldThrowException() {
    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> orderService.addOrder(null)
    );
    assertEquals("Order cannot be null.", exception.getMessage());
    verifyNoInteractions(repository, mapper);
  }

  @Test
  @DisplayName("ADD: Should throw IllegalArgumentException when CustomerId is null")
  void addOrder_WhenCustomerIdIsNull_ShouldThrowException() {
    // --- Setup ---
    orderReq.setCustomerId(null);

    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> orderService.addOrder(orderReq)
    );
    assertEquals("Customer ID cannot be null.", exception.getMessage());
    verifyNoInteractions(repository, mapper);
  }

  @Test
  @DisplayName("ADD: Should throw IllegalArgumentException when AddressId is null")
  void addOrder_WhenAddressIdIsNull_ShouldThrowException() {
    // --- Setup ---
    orderReq.setAddressId(null);

    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> orderService.addOrder(orderReq)
    );
    assertEquals("Address ID cannot be null.", exception.getMessage());
    verifyNoInteractions(repository, mapper);
  }

  @Test
  @DisplayName("ADD: Should throw IllegalArgumentException when CardId is null")
  void addOrder_WhenCardIdIsNull_ShouldThrowException() {
    // --- Setup ---
    orderReq.setCardId(null);

    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> orderService.addOrder(orderReq)
    );
    assertEquals("Card ID cannot be null.", exception.getMessage());
    verifyNoInteractions(repository, mapper);
  }

  // ------------------------------------------------------------------
  // Query Method Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("GET_ALL: Should return a list of all Orders")
  void getAllOrders_ReturnsList() {
    // --- Setup Mocks ---
    List<OrderEntity> entityList = List.of(orderEntity);
    List<Order> modelList = List.of(orderModel);
    when(repository.findAll()).thenReturn(entityList);
    when(mapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    List<Order> result = orderService.getAllOrders();

    // --- Assert & Verify ---
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(repository, times(1)).findAll();
  }

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should return a list of Orders when found")
  void getOrdersByCustomerId_WhenFound_ReturnsList() {
    // --- Setup Mocks ---
    List<OrderEntity> entityList = List.of(orderEntity);
    List<Order> modelList = List.of(orderModel);
    when(repository.findByCustomerId(customerId)).thenReturn(entityList);
    when(mapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    List<Order> result = orderService.getOrdersByCustomerId(customerId);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(repository, times(1)).findByCustomerId(customerId);
  }

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should return an empty list when no orders are found")
  void getOrdersByCustomerId_WhenNotFound_ReturnsEmptyList() {
    // --- Setup Mocks ---
    List<OrderEntity> emptyEntityList = Collections.emptyList();
    List<Order> emptyModelList = Collections.emptyList();
    when(repository.findByCustomerId(customerId)).thenReturn(emptyEntityList);
    when(mapper.entityToModelList(emptyEntityList)).thenReturn(emptyModelList);

    // --- Execute ---
    List<Order> result = orderService.getOrdersByCustomerId(customerId);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(repository, times(1)).findByCustomerId(customerId);
  }

  @Test
  @DisplayName("GET_BY_ORDER_ID: Should return Optional<Order> when found")
  void getOrderId_WhenFound_ReturnsOptionalOrderBy() {
    // --- Setup Mocks ---
    when(repository.findById(orderId)).thenReturn(Optional.of(orderEntity));
    when(mapper.entityToModel(orderEntity)).thenReturn(orderModel);

    // --- Execute ---
    Optional<Order> result = orderService.getOrderById(orderId);

    // --- Assert & Verify ---
    assertTrue(result.isPresent());
    assertEquals(orderId, result.get().getId());
    verify(repository, times(1)).findById(orderId);
  }

  @Test
  @DisplayName("GET_BY_ORDER_ID: Should return Optional.empty() when not found")
  void getOrderById_WhenNotFound_ReturnsEmptyOptional() {
    // --- Setup Mocks ---
    when(repository.findById(orderId)).thenReturn(Optional.empty());

    // --- Execute ---
    Optional<Order> result = orderService.getOrderById(orderId);

    // --- Assert & Verify ---
    assertFalse(result.isPresent());
    verify(repository, times(1)).findById(orderId);
    verify(mapper, never()).entityToModel(any());
  }
}