package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.CartEntity;
import com.example.ecommercedemo.entity.ItemEntity;
import com.example.ecommercedemo.entity.ProductEntity;
import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.mappers.ItemMapper;
import com.example.ecommercedemo.model.Item;
import com.example.ecommercedemo.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Assuming your class is named 'CartService'
 * and 'repository' and 'mapper' are its dependencies.
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock
  private CartRepository repository; // Mock dependency

  @Mock
  private ItemMapper itemMapper; // Mock dependency

  @InjectMocks
  private CartServiceImpl cartService; // The class under test

  // ArgumentCaptor to capture the entity passed to repository.save()
  @Captor
  private ArgumentCaptor<CartEntity> cartEntityCaptor;

  // --- Test Data ---
  private UUID customerId;
  private UUID existingProductId;
  private UUID newProductId;
  private UserEntity userEntity;
  private CartEntity cartEntity;
  private ItemEntity existingItemEntity;
  private ProductEntity existingProduct;

  /**
   * Helper method to simulate the private getCartEntityByCustomerId method.
   * We mock the repository call that this private method would make.
   */
  private void mockGetCartEntity() {
    // This simulates the 'getCartEntityByCustomerId' logic
    // We are assuming it finds a cart associated with the customerId.
    // The Optional is handled inside the (unseen) private method.
    when(repository.findByCustomerId(customerId)).thenReturn(java.util.Optional.of(cartEntity));
  }

  /**
   * Helper to mock the repository.save() call.
   * It uses AdditionalAnswers.returnsFirstArg() to return the
   * same CartEntity that was passed into the save method.
   */
  private void mockSaveAndMap() {
    // Mock save to return the entity it was passed
    when(repository.save(any(CartEntity.class))).then(AdditionalAnswers.returnsFirstArg());

    // Mock the mapper to just return a dummy list (we verify the input list)
    when(itemMapper.entityToModelList(anyList())).thenReturn(new ArrayList<Item>());
  }

  @BeforeEach
  void setUp() {
    // Setup common test data
    customerId = UUID.randomUUID();
    existingProductId = UUID.randomUUID();
    newProductId = UUID.randomUUID();

    // Setup the user / customer entity
    userEntity = new UserEntity();
    userEntity.setId(UUID.randomUUID());
    userEntity.setUsername("username");

    // Setup the product entity that's referred to by the item in the cart
    existingProduct = new ProductEntity();
    existingProduct.setId(existingProductId);
    existingProduct.setName("Old Product");

    // Setup the item entity that's "in the cart"
    existingItemEntity = new ItemEntity();
    existingItemEntity.setProduct(existingProduct);
    existingItemEntity.setQuantity(1);
    existingItemEntity.setPrice(new BigDecimal("10.00"));

    // Setup the cart entity
    cartEntity = new CartEntity();
    cartEntity.setUser(userEntity);
    // IMPORTANT: Initialize with a *mutable* list for tests
    cartEntity.setItems(new ArrayList<>(List.of(existingItemEntity)));
  }

  @Test
  @DisplayName("Should update quantity and price when item already exists in cart")
  void addOrReplaceItems_WhenItemExists_ShouldUpdateItem() {
    // --- Setup ---
    // Mock the call to get the cart
    mockGetCartEntity();
    // Mock the save and map calls
    mockSaveAndMap();

    // Create an 'Item' DTO that matches the existing product
    Item updatedItemDto = new Item();
    updatedItemDto.setProductId(existingProductId); // Same ID
    updatedItemDto.setQuantity(5); // New quantity
    updatedItemDto.setUnitPrice("99.50"); // New price

    // --- Execute ---
    cartService.addOrReplaceItemsByCustomerId(customerId, updatedItemDto);

    // --- Verify ---
    // Verify that repository.save() was called
    verify(repository).save(cartEntityCaptor.capture());

    // Verify that the mapper was *not* called to create a new entity
    verify(itemMapper, never()).modelToEntity(any(Item.class));

    // Get the cart entity that was "saved"
    CartEntity savedCart = cartEntityCaptor.getValue();

    // Assert: The list size should still be 1
    assertEquals(1, savedCart.getItems().size());

    // Assert: The item in the list should have updated values
    ItemEntity updatedEntity = savedCart.getItems().get(0);
    assertEquals(5, updatedEntity.getQuantity());
    assertEquals(new BigDecimal("99.50"), updatedEntity.getPrice());
    assertEquals(existingProductId, updatedEntity.getProduct().getId());
  }

  @Test
  @DisplayName("Should add new item when item does not exist in cart")
  void addOrReplaceItems_WhenItemIsNew_ShouldAddItemToList() {
    // --- Setup ---
    // Mock the call to get the cart (which has 1 item already)
    mockGetCartEntity();

    // This is the new item entity that the mapper will "create"
    ProductEntity newProduct = new ProductEntity();
    newProduct.setId(newProductId);

    ItemEntity newItemEntity = new ItemEntity();
    newItemEntity.setProduct(newProduct);
    newItemEntity.setQuantity(2);
    newItemEntity.setPrice(new BigDecimal("20.00"));

    // Mock the mapper to return this new entity
    when(itemMapper.modelToEntity(any(Item.class))).thenReturn(newItemEntity);

    // Mock the save and map calls
    mockSaveAndMap();

    // Create a 'Item' DTO for the *new* product
    Item newItemDto = new Item();
    newItemDto.setProductId(newProductId); // Different ID
    newItemDto.setQuantity(2);
    newItemDto.setUnitPrice("20.00");

    // --- Execute ---
    cartService.addOrReplaceItemsByCustomerId(customerId, newItemDto);

    // --- Verify ---
    // Verify that repository.save() was called
    verify(repository).save(cartEntityCaptor.capture());

    // Verify that the mapper *was* called to create the new entity
    verify(itemMapper, times(1)).modelToEntity(newItemDto);

    // Get the cart entity that was "saved"
    CartEntity savedCart = cartEntityCaptor.getValue();

    // Assert: The list size should now be 2
    assertEquals(2, savedCart.getItems().size());

    // Assert: The new item is in the list
    // (Order isn't guaranteed, so we check if *any* item matches)
    assertTrue(savedCart.getItems().stream()
        .anyMatch(item -> item.getProduct().getId().equals(newProductId)));
  }

  @Test
  @DisplayName("Should add item to a cart with a null item list")
  void addOrReplaceItems_WhenCartItemsListIsNull_ShouldCreateListAndAddItem() {
    // --- Setup ---
    // Create an empty cart and set its items list to null
    CartEntity emptyCart = new CartEntity();
    emptyCart.setUser(userEntity);
    emptyCart.setItems(null); // <-- This is the key part of this test

    // Mock the repository to return this empty cart
    when(repository.findByCustomerId(customerId)).thenReturn(java.util.Optional.of(emptyCart));

    // This is the new item entity that the mapper will "create"
    ProductEntity newProduct = new ProductEntity();
    newProduct.setId(newProductId);
    ItemEntity newItemEntity = new ItemEntity();
    newItemEntity.setProduct(newProduct);
    newItemEntity.setQuantity(1);

    // Mock the mapper and save
    when(itemMapper.modelToEntity(any(Item.class))).thenReturn(newItemEntity);
    mockSaveAndMap();

    // Create a 'Item' DTO for the new product
    Item newItemDto = new Item();
    newItemDto.setProductId(newProductId);
    newItemDto.setQuantity(1);
    newItemDto.setUnitPrice("10.00");

    // --- Execute ---
    cartService.addOrReplaceItemsByCustomerId(customerId, newItemDto);

    // --- Verify ---
    // Verify save was called
    verify(repository).save(cartEntityCaptor.capture());

    // Verify mapper was called
    verify(itemMapper, times(1)).modelToEntity(newItemDto);

    // Get the saved cart
    CartEntity savedCart = cartEntityCaptor.getValue();

    // Assert: The cart's item list should no longer be null
    assertNotNull(savedCart.getItems());

    // Assert: The list size should be 1
    assertEquals(1, savedCart.getItems().size());
    assertEquals(newItemEntity, savedCart.getItems().get(0));
  }

  // ========== Exception testing ==========

  @Test
  @DisplayName("Should throw IllegalArgumentException when CustomerId is null")
  void addOrReplaceItems_WhenCustomerIdIsNull_ShouldThrowException() {
    // --- Setup ---
    Item item = new Item();
    item.setProductId(UUID.randomUUID()); // Set with random productId
    item.setQuantity(1);
    item.setUnitPrice("10.00");

    // --- Execute & Assert ---
    // Assert that the expected exception is thrown when 'customerId' is null
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> cartService.addOrReplaceItemsByCustomerId(null, item)
    );

    // Optional: Verify the exception message
    assertEquals("CustomerId cannot be null.", exception.getMessage());

    // --- Verify ---
    // Ensure no database or mapper interactions occurred
    verifyNoInteractions(repository);
    verifyNoInteractions(itemMapper);
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when Item is null")
  void addOrReplaceItems_WhenItemIsNull_ShouldThrowException() {
    // --- Setup ---
    UUID customerId = UUID.randomUUID();
    Item nullItem = null;

    // --- Execute & Assert ---
    // Assert that the expected exception is thrown when 'item' is null
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> cartService.addOrReplaceItemsByCustomerId(customerId, nullItem)
    );

    // Optional: Verify the exception message
    assertEquals("Item cannot be null.", exception.getMessage());

    // --- Verify ---
    // Ensure no database or mapper interactions occurred
    verifyNoInteractions(repository);
    verifyNoInteractions(itemMapper);
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when ProductId is null")
  void addOrReplaceItems_WhenProductIdIsNull_ShouldThrowException() {
    // --- Setup ---
    UUID customerId = UUID.randomUUID();
    Item itemWithNullProductId = new Item();
    itemWithNullProductId.setProductId(null); // Set the productId to null
    itemWithNullProductId.setQuantity(1);
    itemWithNullProductId.setUnitPrice("10.00");

    // --- Execute & Assert ---
    // Assert that the expected exception is thrown
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> cartService.addOrReplaceItemsByCustomerId(customerId, itemWithNullProductId)
    );

    // Optional: Verify the exception message
    assertEquals("ProductId cannot be null.", exception.getMessage());

    // --- Verify ---
    // Ensure no database or mapper interactions occurred
    verifyNoInteractions(repository);
    verifyNoInteractions(itemMapper);
  }
}
