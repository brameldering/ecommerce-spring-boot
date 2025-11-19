package com.example.ecommercedemo.payment;

import com.example.ecommercedemo.order.OrderEntity;
import com.example.ecommercedemo.model.Authorization;
import com.example.ecommercedemo.model.PaymentReq;
import com.example.ecommercedemo.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock
  private PaymentRepository paymentRepository;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private AuthorizationMapper authorizationMapper;

  @InjectMocks
  private PaymentServiceImpl paymentService;

  // --- Test Data ---
  private UUID orderId;
  private AuthorizationEntity authorizationEntity;
  private Authorization authorizationModel;
  private OrderEntity orderEntity;
  private PaymentReq paymentReq;

  @BeforeEach
  void setUp() {
    orderId = UUID.randomUUID();

    // 1. Setup Entities
    authorizationEntity = new AuthorizationEntity();
    // Assuming AuthorizationEntity has a set of properties

    orderEntity = new OrderEntity();
    orderEntity.setId(orderId);
    orderEntity.setAuthorizationEntity(authorizationEntity); // Link Authorization to Order

    // 2. Setup Models/DTOs
    authorizationModel = new Authorization();
    // Assuming Authorization model has a set of properties

    paymentReq = new PaymentReq();
    // Set properties for the request
  }

  // ------------------------------------------------------------------
  // authorize Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("AUTHORIZE: Should return null as the method is currently unimplemented")
  void authorize_ReturnsNull() {
    // --- Execute ---
    Authorization result = paymentService.authorize(orderId, paymentReq);

    // --- Assert & Verify ---
    assertNull(result);
    verifyNoInteractions(paymentRepository, orderRepository, authorizationMapper);
  }

  // ------------------------------------------------------------------
  // getOrdersPaymentAuthorization Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("GET_AUTH: Should return Optional<Authorization> when order and authorization are found")
  void getAuthorization_WhenFound_ReturnsOptionalAuthorizationByOrderId() {
    // --- Setup Mocks ---
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
    when(authorizationMapper.entityToModel(authorizationEntity)).thenReturn(authorizationModel);

    // --- Execute ---
    Optional<Authorization> result = paymentService.getAuthorizationByOrderId(orderId);

    // --- Assert & Verify ---
    assertTrue(result.isPresent());
    assertEquals(authorizationModel, result.get());
    verify(orderRepository, times(1)).findById(orderId);
    verify(authorizationMapper, times(1)).entityToModel(authorizationEntity);
  }

  @Test
  @DisplayName("GET_AUTH: Should return Optional.empty() when the Order is not found")
  void getAuthorization_ByOrderId_WhenOrderNotFound_ReturnsEmptyOptional() {
    // --- Setup Mocks ---
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    // --- Execute ---
    Optional<Authorization> result = paymentService.getAuthorizationByOrderId(orderId);

    // --- Assert & Verify ---
    assertFalse(result.isPresent());
    verify(orderRepository, times(1)).findById(orderId);
    verifyNoInteractions(authorizationMapper); // Mapper should never be called
  }

  @Test
  @DisplayName("GET_AUTH: Should return Optional.empty() when Order is found but Authorization is null")
  void getAuthorization_WhenAuthorizationByOrderIdIsNull_ReturnsEmptyOptional() {
    // --- Setup ---
    // Set the authorization entity on the order to null (not authorized yet)
    OrderEntity orderWithoutAuth = new OrderEntity();
    orderWithoutAuth.setId(orderId);
    orderWithoutAuth.setAuthorizationEntity(null);

    // --- Setup Mocks ---
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderWithoutAuth));

    // --- Execute ---
    Optional<Authorization> result = paymentService.getAuthorizationByOrderId(orderId);

    // --- Assert & Verify ---
    assertFalse(result.isPresent());
    verify(orderRepository, times(1)).findById(orderId);
    verifyNoInteractions(authorizationMapper); // Mapper should never be called
  }
}