package com.example.ecommercedemo.order;

import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.model.OrderReq;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@WithMockUser(username = "testuser")
public class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private OrderService orderService;

  @MockBean
  private JwtDecoder jwtDecoder;

  // Mock HATEOAS Assembler: returns the object(s) as is for simple testing
  @MockBean
  private OrderRepresentationModelAssembler orderAssembler;

  private final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private final UUID ORDER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
  private final UUID ADDRESS_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
  private final UUID CARD_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

  private OrderReq orderReq;
  private Order mockOrder;
  private List<Order> mockOrderList;

  @BeforeEach
  void setUp() {
    // Setup mock request data
    orderReq = new OrderReq()
        .addressId(ADDRESS_ID)
        .cardId(CARD_ID);

    // Setup mock response data
    mockOrder = new Order()
        .id(ORDER_ID)
        .status(Order.StatusEnum.CREATED)
        .total("99.99");

    mockOrderList = List.of(mockOrder, new Order().id(UUID.randomUUID()).status(Order.StatusEnum.PAID).total("50.00"));

    // Mock the assemblers to return the input object(s)
    when(orderAssembler.toModel(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(orderAssembler.toModelList(any())).thenAnswer(invocation -> invocation.getArgument(0));
  }

  // --- 1. POST /customers/{id}/orders (Create Order) ---
  @Test
  void addOrder_shouldReturn201Created_andOrder() throws Exception {
    // Arrange
    when(orderService.addOrder(eq(CUSTOMER_ID), any(OrderReq.class))).thenReturn(mockOrder);

    // Act & Assert
    mockMvc.perform(post("/api/v1/customers/{id}/orders", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderReq))
            .with(csrf())) // CSRF for POST requests
        .andExpect(status().isCreated()) // Expect 201 CREATED
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(ORDER_ID.toString()))
        .andExpect(jsonPath("$.total").value("99.99"));

    // Verify service method was called
    verify(orderService, times(1)).addOrder(eq(CUSTOMER_ID), any(OrderReq.class));
  }

  // --- 2. GET /customers/{id}/orders (Get Customer Orders) ---
  @Test
  void getCustomerOrders_shouldReturn200Ok_andListOfOrders() throws Exception {
    // Arrange
    when(orderService.getOrdersByCustomerId(CUSTOMER_ID)).thenReturn(mockOrderList);

    // Act & Assert
    mockMvc.perform(get("/api/v1/customers/{id}/orders", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // Expect 200 OK
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].status").value("CREATED"));

    verify(orderService, times(1)).getOrdersByCustomerId(CUSTOMER_ID);
  }

  @Test
  void getCustomerOrders_shouldReturn200Ok_andEmptyList_whenNoOrdersFound() throws Exception {
    // Arrange
    when(orderService.getOrdersByCustomerId(CUSTOMER_ID)).thenReturn(List.of());

    // Act & Assert
    mockMvc.perform(get("/api/v1/customers/{id}/orders", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));

    verify(orderService, times(1)).getOrdersByCustomerId(CUSTOMER_ID);
  }

  // --- 3. GET /orders/{id} (Get Order by ID) ---
  @Test
  void getByOrderId_shouldReturn200Ok_whenOrderExists() throws Exception {
    // Arrange
    when(orderService.getOrderById(ORDER_ID)).thenReturn(Optional.of(mockOrder));

    // Act & Assert
    mockMvc.perform(get("/api/v1/orders/{id}", ORDER_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // Expect 200 OK
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(ORDER_ID.toString()))
        .andExpect(jsonPath("$.status").value("CREATED"));

    verify(orderService, times(1)).getOrderById(ORDER_ID);
  }

  @Test
  void getByOrderId_shouldReturn404NotFound_whenOrderDoesNotExist() throws Exception {
    // Arrange
    when(orderService.getOrderById(ORDER_ID)).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(get("/api/v1/orders/{id}", ORDER_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound()) // Expect 404 NOT FOUND
        .andExpect(content().string(""));

    verify(orderService, times(1)).getOrderById(ORDER_ID);
  }
}