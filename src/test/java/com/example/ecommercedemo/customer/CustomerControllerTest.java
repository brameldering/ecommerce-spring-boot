package com.example.ecommercedemo.customer;

import com.example.ecommercedemo.model.Customer;
import com.example.ecommercedemo.model.CustomerReq;
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

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@WithMockUser(username = "testuser")
public class CustomerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CustomerService customerService;

  // Mock HATEOAS Assembler: returns the object as is for simple testing
  @MockBean
  private CustomerRepresentationModelAssembler customerAssembler;

  // Mock the JwtDecoder to allow the OAuth2 Security Filter Chain to initialize
  @MockBean
  private JwtDecoder jwtDecoder;

  private final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private CustomerReq customerReq;
  private Customer mockCustomer;

  @BeforeEach
  void setUp() {
    // Setup mock data
    customerReq = new CustomerReq()
        .username("testuser")
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com");

    mockCustomer = new Customer()
        .id(CUSTOMER_ID)
        .username("testuser")
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com");

    // Mock the assembler to return the input Customer object (without adding actual links)
    when(customerAssembler.toModel(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
  }

  // --- 1. POST /customers (Create Customer) ---
  @Test
  void createCustomer_shouldReturn201Created_andCustomer() throws Exception {
    // Arrange
    when(customerService.createCustomer(any(CustomerReq.class))).thenReturn(mockCustomer);

    // Act & Assert
    mockMvc.perform(post("/api/v1/customers")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customerReq))
            .with(csrf())) // CSRF for POST requests
        .andExpect(status().isCreated()) // Expect 201 CREATED
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(CUSTOMER_ID.toString()))
        .andExpect(jsonPath("$.username").value("testuser"));

    // Verify service method was called
    verify(customerService, times(1)).createCustomer(any(CustomerReq.class));
  }

  // --- 2. GET /customers/{id} (Get Customer) ---
  @Test
  void getCustomerById_shouldReturn200Ok_whenCustomerExists() throws Exception {
    // Arrange
    when(customerService.getCustomerById(CUSTOMER_ID)).thenReturn(Optional.of(mockCustomer));

    // Act & Assert
    mockMvc.perform(get("/api/v1/customers/{id}", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // Expect 200 OK
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(CUSTOMER_ID.toString()))
        .andExpect(jsonPath("$.firstName").value("John"));
  }

  @Test
  void getCustomerById_shouldReturn404NotFound_whenCustomerDoesNotExist() throws Exception {
    // Arrange
    when(customerService.getCustomerById(CUSTOMER_ID)).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(get("/api/v1/customers/{id}", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound()) // Expect 404 NOT FOUND
        .andExpect(content().string("")); // Response body should be empty
  }

  // --- 3. PUT /customers/{id} (Update Customer) ---
  @Test
  void updateCustomer_shouldReturn200Ok_andUpdatedCustomer() throws Exception {
    // Arrange
    Customer updatedCustomer = mockCustomer.firstName("Jane");
    when(customerService.updateCustomer(eq(CUSTOMER_ID), any(CustomerReq.class))).thenReturn(updatedCustomer);

    // Act & Assert
    mockMvc.perform(put("/api/v1/customers/{id}", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customerReq.firstName("Jane")))
            .with(csrf())) // CSRF for PUT requests
        .andExpect(status().isOk()) // Expect 200 OK
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(CUSTOMER_ID.toString()))
        .andExpect(jsonPath("$.firstName").value("Jane")); // Verify updated field

    // Verify service method was called
    verify(customerService, times(1)).updateCustomer(eq(CUSTOMER_ID), any(CustomerReq.class));
  }

  // --- 4. DELETE /customers/{id} (Delete Customer) ---
  @Test
  void deleteCustomerById_shouldReturn202Accepted() throws Exception {
    // Arrange
    doNothing().when(customerService).deleteCustomerById(CUSTOMER_ID);

    // Act & Assert
    mockMvc.perform(delete("/api/v1/customers/{id}", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON)
            .with(csrf())) // CSRF for DELETE requests
        .andExpect(status().isAccepted()) // Expect 202 ACCEPTED
        .andExpect(content().string("")); // Response body should be empty

    // Verify service method was called
    verify(customerService, times(1)).deleteCustomerById(CUSTOMER_ID);
  }
}