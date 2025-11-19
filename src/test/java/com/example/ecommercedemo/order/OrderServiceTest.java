package com.example.ecommercedemo.order;

import com.example.ecommercedemo.customer.CustomerEntity;
import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.model.OrderReq;
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
  private OrderRepository orderRepository;

  @Mock
  private OrderMapper orderMapper;

  @InjectMocks
  private OrderServiceImpl orderService;

  // --- Test Data ---
  private UUID orderId;
  private UUID customerId;
  private UUID addressId;
  private UUID cardId;
  private CustomerEntity customerEntity;
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
    customerEntity = new CustomerEntity();
    customerEntity.setId(customerId);
    customerEntity.setFirstName("FirstName");
    customerEntity.setLastName("LastName");

    orderEntity = new OrderEntity();
    orderEntity.setId(orderId);
    orderEntity.setCustomerEntity(customerEntity);
    // Set other properties as needed for a real entity

    // 2. Setup Models/DTOs
    orderModel = new Order();
    orderModel.setId(orderId);
    // Set other properties as needed for a real model

    orderReq = new OrderReq();
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
    when(orderRepository.insert(customerId, orderReq)).thenReturn(orderEntity);
    when(orderMapper.entityToModel(orderEntity)).thenReturn(orderModel);

    // --- Execute ---
    Order result = orderService.addOrder(customerId, orderReq);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertEquals(orderModel.getId(), result.getId());
    verify(orderRepository, times(1)).insert(customerId, orderReq);
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
        () -> orderService.addOrder(customerId, null)
    );
    assertEquals("Order cannot be null.", exception.getMessage());
    verifyNoInteractions(orderRepository, orderMapper);
  }

  @Test
  @DisplayName("ADD: Should throw IllegalArgumentException when CustomerId is null")
  void addOrder_WhenCustomerIdIsNull_ShouldThrowException() {

    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> orderService.addOrder(null, orderReq)
    );
    assertEquals("Customer ID cannot be null.", exception.getMessage());
    verifyNoInteractions(orderRepository, orderMapper);
  }

  @Test
  @DisplayName("ADD: Should throw IllegalArgumentException when AddressId is null")
  void addOrder_WhenAddressIdIsNull_ShouldThrowException() {
    // --- Setup ---
    orderReq.setAddressId(null);

    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> orderService.addOrder(customerId, orderReq)
    );
    assertEquals("Address ID cannot be null.", exception.getMessage());
    verifyNoInteractions(orderRepository, orderMapper);
  }

  @Test
  @DisplayName("ADD: Should throw IllegalArgumentException when CardId is null")
  void addOrder_WhenCardIdIsNull_ShouldThrowException() {
    // --- Setup ---
    orderReq.setCardId(null);

    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> orderService.addOrder(customerId, orderReq)
    );
    assertEquals("Card ID cannot be null.", exception.getMessage());
    verifyNoInteractions(orderRepository, orderMapper);
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
    when(orderRepository.findAll()).thenReturn(entityList);
    when(orderMapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    List<Order> result = orderService.getAllOrders();

    // --- Assert & Verify ---
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(orderRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should return a list of Orders when found")
  void getOrdersByCustomerId_WhenFound_ReturnsList() {
    // --- Setup Mocks ---
    List<OrderEntity> entityList = List.of(orderEntity);
    List<Order> modelList = List.of(orderModel);
    when(orderRepository.findByCustomerId(customerId)).thenReturn(entityList);
    when(orderMapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    List<Order> result = orderService.getOrdersByCustomerId(customerId);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(orderRepository, times(1)).findByCustomerId(customerId);
  }

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should return an empty list when no orders are found")
  void getOrdersByCustomerId_WhenNotFound_ReturnsEmptyList() {
    // --- Setup Mocks ---
    List<OrderEntity> emptyEntityList = Collections.emptyList();
    List<Order> emptyModelList = Collections.emptyList();
    when(orderRepository.findByCustomerId(customerId)).thenReturn(emptyEntityList);
    when(orderMapper.entityToModelList(emptyEntityList)).thenReturn(emptyModelList);

    // --- Execute ---
    List<Order> result = orderService.getOrdersByCustomerId(customerId);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(orderRepository, times(1)).findByCustomerId(customerId);
  }

  @Test
  @DisplayName("GET_BY_ORDER_ID: Should return Optional<Order> when found")
  void getOrderId_WhenFound_ReturnsOptionalOrderBy() {
    // --- Setup Mocks ---
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
    when(orderMapper.entityToModel(orderEntity)).thenReturn(orderModel);

    // --- Execute ---
    Optional<Order> result = orderService.getOrderById(orderId);

    // --- Assert & Verify ---
    assertTrue(result.isPresent());
    assertEquals(orderId, result.get().getId());
    verify(orderRepository, times(1)).findById(orderId);
  }

  @Test
  @DisplayName("GET_BY_ORDER_ID: Should return Optional.empty() when not found")
  void getOrderById_WhenNotFound_ReturnsEmptyOptional() {
    // --- Setup Mocks ---
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    // --- Execute ---
    Optional<Order> result = orderService.getOrderById(orderId);

    // --- Assert & Verify ---
    assertFalse(result.isPresent());
    verify(orderRepository, times(1)).findById(orderId);
    verify(orderMapper, never()).entityToModel(any());
  }
}