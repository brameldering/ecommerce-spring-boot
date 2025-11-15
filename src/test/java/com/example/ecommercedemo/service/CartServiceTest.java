package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.CartEntity;
import com.example.ecommercedemo.entity.CustomerEntity;
import com.example.ecommercedemo.entity.ItemEntity;
import com.example.ecommercedemo.entity.ProductEntity;
import com.example.ecommercedemo.exceptions.ItemAlreadyExistsException;
import com.example.ecommercedemo.exceptions.ItemNotFoundException;
import com.example.ecommercedemo.mappers.CartMapper;
import com.example.ecommercedemo.mappers.ItemMapper;
import com.example.ecommercedemo.model.Item;
import com.example.ecommercedemo.repository.CartRepository;
import com.example.ecommercedemo.repository.ItemRepository;
import com.example.ecommercedemo.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Assuming your class is named 'CartService'
 * and 'repository' and 'mapper' are its dependencies.
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock
  private CartRepository cartRepository; // Mock dependency

  @Mock
  private ItemRepository itemRepository; // Mock dependency

  @Mock
  private CustomerRepository customerRepository; // Mock dependency

  @Mock
  private CartMapper cartMapper; // Mock dependency

  @Mock
  private ItemMapper itemMapper; // Mock dependency

  @InjectMocks
  private CartServiceImpl cartService; // The class under test

  // ArgumentCaptor to capture the entity passed to repository.save()
  @Captor
  private ArgumentCaptor<CartEntity> cartEntityCaptor;

//  private final Logger logger = LoggerFactory.getLogger(CartServiceTest.class);

  // --- Test Data ---
  private UUID customerId;
  private UUID existingProductId;
  private UUID newProductId;
  private CustomerEntity customerEntity;
  private CartEntity cartEntity;
  private ItemEntity existingItemEntity;
  private ProductEntity existingProduct;

  /**
   * Helper method to simulate the private getCartEntityByCustomerId method.
   * We mock the repository call that this private method would make.
   */
  private void mockGetCartEntity() {
    // This simulates the 'getCartEntityByCustomerId' logic
    when(cartRepository.findCartAndItemsAndProductsByCustomerId(customerId)).thenReturn(Optional.of(cartEntity));
  }

  @BeforeEach
  void setUp() {
    // Setup common test data
    customerId = UUID.randomUUID();
    existingProductId = UUID.randomUUID();
    newProductId = UUID.randomUUID();

    // Setup the customer entity
    customerEntity = new CustomerEntity();
    customerEntity.setId(UUID.randomUUID());
    customerEntity.setUsername("username");

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
    cartEntity.setCustomer(customerEntity);
    // IMPORTANT: Initialize with a *mutable* list for tests
    cartEntity.setItems(new ArrayList<>(List.of(existingItemEntity)));

    // Ensure CustomerRepo always returns a Customer when looked up by a non-null ID
    // This prevents CustomerNotFoundException when testing validation for other null parameters.
    lenient().when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.of(customerEntity));
    lenient().when(itemRepository.save(any(ItemEntity.class))).then(AdditionalAnswers.returnsFirstArg());
  }

  @Test
  @DisplayName("ADD: Should add a new item when cart is empty")
  void addItemToCart_WhenCartIsEmpty_ShouldAddNewItem() {
    // --- Setup ---
    when(cartRepository.save(any(CartEntity.class))).then(AdditionalAnswers.returnsFirstArg());
    mockGetCartEntity();
    cartEntity.setItems(new ArrayList<>());

    // Create a new item DTO
    Item newItemDto = new Item();
    newItemDto.setProductId(newProductId);
    newItemDto.setQuantity(3);
    newItemDto.setUnitPrice("50.00");

    // Mock the mapper result for the new item
    ProductEntity newProduct = new ProductEntity();
    newProduct.setId(newProductId);
    ItemEntity newItemEntity = new ItemEntity();
    newItemEntity.setProduct(newProduct);

    when(itemMapper.modelToEntity(any(Item.class))).thenReturn(newItemEntity);

    // --- Execute ---
    cartService.addItemToCart(customerId, newItemDto);

    // --- Verify ---
    verify(cartRepository).save(cartEntityCaptor.capture());
    CartEntity savedCart = cartEntityCaptor.getValue();

    // Assert: One item was added
    assertEquals(1, savedCart.getItems().size());
    // Assert: Mapper was called
    verify(itemMapper, times(1)).modelToEntity(newItemDto);
  }

  @Test
  @DisplayName("ADD: Should throw ItemAlreadyExistsException if item already exists")
  void addItemToCart_WhenItemExists_ShouldThrowException() {
    // --- Setup ---
//    when(cartRepository.save(any(CartEntity.class))).then(AdditionalAnswers.returnsFirstArg());
    mockGetCartEntity(); // Cart already contains existingProductId
    Item itemDto = new Item(); // Create an item DTO that matches the existing product
    itemDto.setProductId(existingProductId);

    // --- Execute & Assert ---
    ItemAlreadyExistsException exception = assertThrows(
        ItemAlreadyExistsException.class,
        () -> cartService.addItemToCart(customerId, itemDto)
    );

    // Verify the exception message
    assertTrue(exception.getMessage().contains("already exists"));

    // Verify no save occurred
    verify(cartRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should update quantity and price when item already exists in cart")
  void addOrReplaceItems_WhenItemExists_ShouldUpdateItem() {
    // --- Setup ---
    when(cartRepository.save(any(CartEntity.class))).then(AdditionalAnswers.returnsFirstArg());
//    when(itemMapper.entityToModelList(anyList())).thenReturn(new ArrayList<Item>());
    // Mock the call to get the cart
    mockGetCartEntity();

    // Create an 'Item' DTO that matches the existing product
    Item updatedItemDto = new Item();
    updatedItemDto.setProductId(existingProductId); // Same ID
    updatedItemDto.setQuantity(5); // New quantity
    updatedItemDto.setUnitPrice("99.50"); // New price

    // --- Execute ---
    cartService.replaceItemInCart(customerId, updatedItemDto);

    // --- Verify ---
    // Verify that repository.save() was called
    verify(cartRepository).save(cartEntityCaptor.capture());

    // Verify that the mapper was *not* called to create a new entity
    verify(itemMapper, never()).modelToEntity(any(Item.class));

    // Get the cart entity that was "saved"
    CartEntity savedCart = cartEntityCaptor.getValue();

    // Assert: The list size should still be 1
    assertEquals(1, savedCart.getItems().size());

    // Assert: The item in the list should have updated values
    ItemEntity updatedEntity = savedCart.getItems().getFirst();
    assertEquals(5, updatedEntity.getQuantity());
    assertEquals(new BigDecimal("99.50"), updatedEntity.getPrice());
    assertEquals(existingProductId, updatedEntity.getProduct().getId());
  }

  @Test
  @DisplayName("Should add new item when item does not exist in cart")
  void addOrReplaceItems_WhenItemIsNew_ShouldAddItemToList() {
    // --- Setup ---
    when(cartRepository.save(any(CartEntity.class))).then(AdditionalAnswers.returnsFirstArg());
    // Mock the call to get the cart (which has 1 item already)
    mockGetCartEntity();

    // This is the new item entity that the mapper will "create"
    ProductEntity newProduct = new ProductEntity();
    newProduct.setId(newProductId);

    ItemEntity newItemEntity = new ItemEntity();
    newItemEntity.setProduct(newProduct);
    newItemEntity.setQuantity(2);
    newItemEntity.setPrice(new BigDecimal("20.00"));

    // Create a 'Item' DTO for the *new* product
    Item newItemDto = new Item();
    newItemDto.setProductId(newProductId); // Different ID
    newItemDto.setQuantity(2);
    newItemDto.setUnitPrice("20.00");

    // Mock the mapper to return this new entity
    when(itemMapper.modelToEntity(any(Item.class))).thenReturn(newItemEntity);

    // --- Execute ---
    cartService.replaceItemInCart(customerId, newItemDto);

    // --- Verify ---
    // Verify that repository.save() was called
    verify(cartRepository).save(cartEntityCaptor.capture());

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
    when(cartRepository.save(any(CartEntity.class))).then(AdditionalAnswers.returnsFirstArg());
    // Create an empty cart and set its items list to null
    CartEntity emptyCart = new CartEntity();
    emptyCart.setCustomer(customerEntity);
    emptyCart.setItems(null); // <-- This is the key part of this test

    // Mock the repository to return this empty cart
    when(cartRepository.findCartAndItemsAndProductsByCustomerId(customerId)).thenReturn(java.util.Optional.of(emptyCart));

    // This is the new item entity that the mapper will "create"
    ProductEntity newProduct = new ProductEntity();
    newProduct.setId(newProductId);
    ItemEntity newItemEntity = new ItemEntity();
    newItemEntity.setProduct(newProduct);
    newItemEntity.setQuantity(1);

    // Mock the mapper and save
    when(itemMapper.modelToEntity(any(Item.class))).thenReturn(newItemEntity);

    // Create a 'Item' DTO for the new product
    Item newItemDto = new Item();
    newItemDto.setProductId(newProductId);
    newItemDto.setQuantity(1);
    newItemDto.setUnitPrice("10.00");

    // --- Execute ---
    cartService.replaceItemInCart(customerId, newItemDto);

    // --- Verify ---
    // Verify save was called
    verify(cartRepository).save(cartEntityCaptor.capture());

    // Verify mapper was called
    verify(itemMapper, times(1)).modelToEntity(newItemDto);

    // Get the saved cart
    CartEntity savedCart = cartEntityCaptor.getValue();

    // Assert: The cart's item list should no longer be null
    assertNotNull(savedCart.getItems());

    // Assert: The list size should be 1
    assertEquals(1, savedCart.getItems().size());
    assertEquals(newItemEntity, savedCart.getItems().getFirst());
  }

  @Test
  @DisplayName("GET_ITEM_BY_ID: Should return Item when product is found in cart")
  void getCartItemByProductId_WhenItemExists_ShouldReturnItem() {
    // --- Setup ---
    mockGetCartEntity(); // Cart has item with existingProductId

    // Mock the final mapping DTO return
    Item expectedItemDto = new Item();
    expectedItemDto.setProductId(existingProductId);
    when(itemMapper.entityToModel(any(ItemEntity.class))).thenReturn(expectedItemDto);

    // --- Execute ---
    Item result = cartService.getCartItemByProductId(customerId, existingProductId);

    // --- Verify ---
    assertNotNull(result);
    assertEquals(existingProductId, result.getProductId());

    // Verify that the mapper was called with the correct entity
    verify(itemMapper, times(1)).entityToModel(existingItemEntity);
  }

  // ========== Exception testing ==========

  @Test
  @DisplayName("GET_CART_BY_CUSTOMER_ID: Should throw IllegalArgumentException when CustomerId is null")
  void getCartByCustomerId_WhenCustomerIdIsNull_ShouldThrowException() {

    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> cartService.getCartByCustomerId(null)
    );

    // Verify the message
    assertEquals("CustomerId cannot be null.", exception.getMessage());

    // --- Verify ---
    verifyNoInteractions(cartRepository);
  }

  @Test
  @DisplayName("GET_ITEM_BY_ID: Should throw IllegalArgumentException when ProductId is null")
  void getCartItemByProductId_WhenProductIdIsNull_ShouldThrowException() {
    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> cartService.getCartItemByProductId(customerId, null)
    );

    // Verify the message
    assertEquals("ProductId cannot be null.", exception.getMessage());

    // --- Verify ---
    verifyNoInteractions(cartRepository);
  }

  @Test
  @DisplayName("DELETE_ITEM: Should throw IllegalArgumentException when ItemId is null")
  void deleteItemFromCart_WhenItemIdIsNull_ShouldThrowExceptionByCustomerId() {
    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> cartService.deleteItemFromCartByCustomerIdAndProductId(customerId, null)
    );

    // Verify the message
    assertEquals("ProductId cannot be null.", exception.getMessage());

    // --- Verify ---
    verifyNoInteractions(cartRepository);
  }

  @Test
  @DisplayName("ADD/REPLACE: Should throw IllegalArgumentException when CustomerId is null")
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
        () -> cartService.replaceItemInCart(null, item)
    );

    // Verify the exception message (this exception is thrown from the helper: getCartEntityByCustomerId)
    assertEquals("CustomerId cannot be null.", exception.getMessage());

    // --- Verify ---
    // Ensure no database or mapper interactions occurred
    verifyNoInteractions(cartRepository);
    verifyNoInteractions(itemMapper);
  }

  @Test
  @DisplayName("ADD/REPLACE: Should throw IllegalArgumentException when Item is null")
  void addOrReplaceItems_WhenItemIsNull_ShouldThrowException() {
    // --- Setup ---
    UUID customerId = UUID.randomUUID();
    Item nullItem = null;

    // --- Execute & Assert ---
    // Assert that the expected exception is thrown when 'item' is null
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> cartService.replaceItemInCart(customerId, nullItem)
    );

    // Verify the exception message (explicitly thrown in service method)
    assertEquals("Item cannot be null.", exception.getMessage());

    // --- Verify ---
    // Ensure no database or mapper interactions occurred
    verifyNoInteractions(cartRepository);
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
        () -> cartService.replaceItemInCart(customerId, itemWithNullProductId)
    );

    // Verify the exception message (explicitly thrown in service method)
    assertEquals("ProductId cannot be null.", exception.getMessage());

    // --- Verify ---
    // Ensure no database or mapper interactions occurred
    verifyNoInteractions(cartRepository);
    verifyNoInteractions(itemMapper);
  }

  @Test
  @DisplayName("GET_ITEM_BY_ID: Should throw ItemNotFoundException when product is missing from cart")
  void getCartItemByProductId_WhenItemIsMissing_ShouldThrowException() {
    // --- Setup ---
    mockGetCartEntity(); // Cart is fetched successfully

    UUID nonExistentId = UUID.randomUUID();

    // --- Execute & Assert ---
    // Note: Because the implementation uses getUnsafe().throwException,
    // we just check if the call results in an exception.
    assertThrows(
        ItemNotFoundException.class,
        () -> cartService.getCartItemByProductId(customerId, nonExistentId)
    );
  }

  @Test
  @DisplayName("DELETE_CART: Should call deleteById on the cart repository")
  void deleteCartByCustomerId_ShouldCallRepositoryDelete() {
    // --- Setup ---
    // Ensure getCartEntityByCustomerId helper works (fetches entity)
    mockGetCartEntity();

    // --- Execute ---
    cartService.deleteCartByCustomerId(customerId);

    // --- Verify ---
    // 1. Verify the repository's delete method was called with the entity's ID
    verify(cartRepository, times(1)).deleteById(cartEntity.getId());
    // 2. Verify findByCustomerId was called (via mockGetCartEntity)
    verify(cartRepository, times(1)).findCartAndItemsAndProductsByCustomerId(customerId);
  }

}
