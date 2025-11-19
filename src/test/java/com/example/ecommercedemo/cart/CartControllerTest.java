package com.example.ecommercedemo.cart;

import com.example.ecommercedemo.item.ItemRepresentationModelAssembler;
import com.example.ecommercedemo.model.Cart;
import com.example.ecommercedemo.model.Item;
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

@WebMvcTest(CartController.class)
public class CartControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CartService cartService;

  // Mock HATEOAS Assemblers
  @MockBean
  private CartRepresentationModelAssembler cartAssembler;
  @MockBean
  private ItemRepresentationModelAssembler itemAssembler;

  private final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private final UUID PRODUCT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private Item mockItem;
  private Cart mockCart;

  @BeforeEach
  void setUp() {
    // Setup mock data
    mockItem = new Item()
        .productId(PRODUCT_ID)
        .quantity(3)
        .unitPrice("10.50");

    mockCart = new Cart()
        .customerId(CUSTOMER_ID)
        .id(UUID.randomUUID())
        .items(List.of(mockItem));

    // Mock Assemblers to return the input object for testing data structure
    when(cartAssembler.toModel(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(itemAssembler.toModel(any(Item.class), eq(CUSTOMER_ID))).thenAnswer(invocation -> invocation.getArgument(0));

    // Mock toModelList for List<Item>
    when(itemAssembler.toModelList(any(), eq(CUSTOMER_ID))).thenAnswer(invocation -> {
      List<Item> items = invocation.getArgument(0);
      // Simulate mapping by returning the list as is (without adding actual links)
      return items;
    });
  }

  // --- 1. POST /customers/{id}/cart/items (Add Item) ---
  @Test
  void addItemToCart_shouldReturn200Ok_andUpdatedCart() throws Exception {
    // Arrange
    when(cartService.addItemToCart(eq(CUSTOMER_ID), any(Item.class))).thenReturn(mockCart);

    // Act & Assert
    mockMvc.perform(post("/api/v1/customers/{id}/cart/items", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(mockItem)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.customerId").value(CUSTOMER_ID.toString()))
        .andExpect(jsonPath("$.items[0].productId").value(PRODUCT_ID.toString()))
        .andExpect(jsonPath("$.items[0].quantity").value(3));

    verify(cartService, times(1)).addItemToCart(eq(CUSTOMER_ID), any(Item.class));
  }

  // --- 2. PUT /customers/{id}/cart/items (Replace Item) ---
  @Test
  void replaceItemInCart_shouldReturn200Ok_andUpdatedCart() throws Exception {
    // Arrange
    Item replacementItem = mockItem.quantity(5); // Change quantity for update

    // FIX HERE: Update the mockCart object directly, then use it.
    mockCart.setItems(List.of(replacementItem)); // If setItems returns void, call it directly.
    Cart updatedCart = mockCart; // Assign the updated object to a new variable.

    when(cartService.replaceItemInCart(eq(CUSTOMER_ID), any(Item.class))).thenReturn(updatedCart);

    // Act & Assert
    mockMvc.perform(put("/api/v1/customers/{id}/cart/items", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(replacementItem)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[0].quantity").value(5));

    verify(cartService, times(1)).replaceItemInCart(eq(CUSTOMER_ID), any(Item.class));
  }

  // --- 3. GET /customers/{id}/cart (Get Cart) ---
  @Test
  void getCustomerCart_shouldReturn200Ok_whenCartExists() throws Exception {
    // Arrange
    when(cartService.getCartByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(mockCart));

    // Act & Assert
    mockMvc.perform(get("/api/v1/customers/{id}/cart", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.customerId").value(CUSTOMER_ID.toString()));

    verify(cartService, times(1)).getCartByCustomerId(CUSTOMER_ID);
  }

  @Test
  void getCustomerCart_shouldThrowCartNotFoundException_whenCartDoesNotExist() throws Exception {
    // Arrange
    when(cartService.getCartByCustomerId(CUSTOMER_ID)).thenReturn(Optional.empty());

    // Act & Assert
    // The controller throws CartNotFoundException, which Spring maps to 404
    mockMvc.perform(get("/api/v1/customers/{id}/cart", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  // --- 4. GET /customers/{id}/cart/items (Get Cart Items) ---
  @Test
  void getCustomerCartItems_shouldReturn200Ok_andListOfItems() throws Exception {
    // Arrange
    Item secondItem = new Item()
        .productId(UUID.randomUUID())
        .quantity(1)
        .unitPrice("20.00"); // Use any unit price

    List<Item> items = List.of(mockItem, secondItem);

    when(cartService.getCartItemsByCustomerId(CUSTOMER_ID)).thenReturn(items);

    // Act & Assert
    mockMvc.perform(get("/api/v1/customers/{id}/cart/items", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].quantity").value(3));

    verify(cartService, times(1)).getCartItemsByCustomerId(CUSTOMER_ID);
  }

  // --- 5. GET /customers/{id}/cart/items/{productId} (Get Single Item) ---
  @Test
  void getCustomerCartItemByProductId_shouldReturn200Ok_whenItemExists() throws Exception {
    // Arrange
    when(cartService.getCartItemByProductId(CUSTOMER_ID, PRODUCT_ID)).thenReturn(mockItem);

    // Act & Assert
    mockMvc.perform(get("/api/v1/customers/{id}/cart/items/{productId}", CUSTOMER_ID, PRODUCT_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId").value(PRODUCT_ID.toString()));

    verify(cartService, times(1)).getCartItemByProductId(CUSTOMER_ID, PRODUCT_ID);
  }

  @Test
  void getCustomerCartItemByProductId_shouldReturn404NotFound_whenItemDoesNotExist() throws Exception {
    // Arrange
    when(cartService.getCartItemByProductId(CUSTOMER_ID, PRODUCT_ID)).thenReturn(null);

    // Act & Assert
    mockMvc.perform(get("/api/v1/customers/{id}/cart/items/{productId}", CUSTOMER_ID, PRODUCT_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(cartService, times(1)).getCartItemByProductId(CUSTOMER_ID, PRODUCT_ID);
  }

  // --- 6. DELETE /customers/{id}/cart (Delete Cart) ---
  @Test
  void deleteCustomerCart_shouldReturn202Accepted() throws Exception {
    // Arrange
    doNothing().when(cartService).deleteCartByCustomerId(CUSTOMER_ID);

    // Act & Assert
    mockMvc.perform(delete("/api/v1/customers/{id}/cart", CUSTOMER_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isAccepted()); // Matches the return accepted().build()

    verify(cartService, times(1)).deleteCartByCustomerId(CUSTOMER_ID);
  }

  // --- 7. DELETE /customers/{id}/cart/items/{productId} (Delete Single Item) ---
  @Test
  void deleteItemFromCustomerCart_shouldReturn202Accepted() throws Exception {
    // Arrange
    doNothing().when(cartService).deleteItemFromCartByCustomerIdAndProductId(CUSTOMER_ID, PRODUCT_ID);

    // Act & Assert
    mockMvc.perform(delete("/api/v1/customers/{id}/cart/items/{productId}", CUSTOMER_ID, PRODUCT_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isAccepted()); // Matches the return accepted().build()

    verify(cartService, times(1)).deleteItemFromCartByCustomerIdAndProductId(CUSTOMER_ID, PRODUCT_ID);
  }
}