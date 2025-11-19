package com.example.ecommercedemo.admin;

import com.example.ecommercedemo.address.AddressRepresentationModelAssembler;
import com.example.ecommercedemo.card.CardRepresentationModelAssembler;
import com.example.ecommercedemo.customer.CustomerRepresentationModelAssembler;
import com.example.ecommercedemo.order.OrderRepresentationModelAssembler;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.model.Customer;
import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.address.AddressService;
import com.example.ecommercedemo.card.CardService;
import com.example.ecommercedemo.customer.CustomerService;
import com.example.ecommercedemo.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  // Mock Service Dependencies
  @MockBean
  private AddressService addressService;
  @MockBean
  private CardService cardService;
  @MockBean
  private CustomerService customerService;
  @MockBean
  private OrderService orderService;

  // Mock HATEOAS Assembler Dependencies
  // Note: Since the assemblers just transform the object,
  // we'll mock their behavior to return the same input for simplicity in this test.
  @MockBean
  private AddressRepresentationModelAssembler addressAssembler;
  @MockBean
  private CardRepresentationModelAssembler cardAssembler;
  @MockBean
  private CustomerRepresentationModelAssembler customerAssembler;
  @MockBean
  private OrderRepresentationModelAssembler orderAssembler;

  // Sample data setup
  private final UUID CUST_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private List<Address> mockAddresses;
  private List<Card> mockCards;
  private List<Customer> mockCustomers;
  private List<Order> mockOrders;

  @BeforeEach
  void setUp() {
    // Initialize Sample Data
    mockAddresses = List.of(new Address().id(UUID.randomUUID()).customerId(CUST_ID).zipcode("1001AA").street("Test Straat 1"));
    mockCards = List.of(new Card().id(UUID.randomUUID()).customerId(CUST_ID).cardNumber("************1234").expires("12/25"));
    mockCustomers = List.of(new Customer().id(CUST_ID).username("testuser").firstName("Test").lastName("User"));
    mockOrders = List.of(new Order().id(UUID.randomUUID()).status(Order.StatusEnum.CREATED).total("100.00"));

    // Mock Assemblers to return the original object for simple list assertion
    when(addressAssembler.toModelList(any())).thenReturn(mockAddresses);
    when(cardAssembler.toModelList(any())).thenReturn(mockCards);
    when(orderAssembler.toModelList(any())).thenReturn(mockOrders);

    // Mock the specific stream/map logic for getAllCustomers
    when(customerAssembler.toModel(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

  }

  // --- Test Cases for /addresses ---

  @Test
  void getAllAddresses_shouldReturnListOfAddresses_whenServiceReturnsData() throws Exception {
    // Arrange
    when(addressService.getAllAddresses()).thenReturn(mockAddresses);

    // Act & Assert
    mockMvc.perform(get("/api/v1/addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        // Basic check for non-empty list structure
        .andExpect(content().json("[{'zipcode':'1001AA'}]"));
  }

  @Test
  void getAllAddresses_shouldReturnEmptyList_whenServiceReturnsNull() throws Exception {
    // Arrange
    when(addressService.getAllAddresses()).thenReturn(null);

    // Act & Assert
    mockMvc.perform(get("/api/v1/addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  // --- Test Cases for /cards ---

  @Test
  void getAllCards_shouldReturnListOfCards_whenServiceReturnsData() throws Exception {
    // Arrange
    when(cardService.getAllCards()).thenReturn(mockCards);

    // Act & Assert
    mockMvc.perform(get("/api/v1/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[{'expires':'12/25'}]"));
  }

  @Test
  void getAllCards_shouldReturnEmptyList_whenServiceReturnsNull() throws Exception {
    // Arrange
    when(cardService.getAllCards()).thenReturn(null);

    // Act & Assert
    mockMvc.perform(get("/api/v1/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  // --- Test Cases for /customers ---

  @Test
  void getAllCustomers_shouldReturnListOfCustomers_whenServiceReturnsData() throws Exception {
    // Arrange
    when(customerService.getAllCustomers()).thenReturn(mockCustomers);

    // Act & Assert
    mockMvc.perform(get("/api/v1/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[{'username':'testuser'}]"));
  }

  @Test
  void getAllCustomers_shouldReturnEmptyList_whenServiceReturnsEmpty() throws Exception {
    // Arrange
    when(customerService.getAllCustomers()).thenReturn(List.of());

    // Act & Assert
    mockMvc.perform(get("/api/v1/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  // --- Test Cases for /orders ---

  @Test
  void getAllOrders_shouldReturnListOfOrders_whenServiceReturnsData() throws Exception {
    // Arrange
    when(orderService.getAllOrders()).thenReturn(mockOrders);

    // Act & Assert
    mockMvc.perform(get("/api/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[{'status':'CREATED'}]"));
  }

  @Test
  void getAllOrders_shouldReturnEmptyList_whenServiceReturnsNull() throws Exception {
    // Arrange
    when(orderService.getAllOrders()).thenReturn(null);

    // Act & Assert
    mockMvc.perform(get("/api/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }
}