package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.CartEntity;
import com.example.ecommercedemo.entity.CustomerEntity;
import com.example.ecommercedemo.entity.ItemEntity;
import com.example.ecommercedemo.exceptions.*;
import com.example.ecommercedemo.mappers.CartMapper;
import com.example.ecommercedemo.mappers.ItemMapper;
import com.example.ecommercedemo.model.Cart;
import com.example.ecommercedemo.repository.CartRepository;
import com.example.ecommercedemo.repository.CustomerRepository;
import com.example.ecommercedemo.repository.ItemRepository;
import com.example.ecommercedemo.model.Item;
import com.example.ecommercedemo.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;
  private final ItemRepository itemRepository;
  private final CustomerRepository customerRepository;
  private final CartMapper cartMapper;
  private final ItemMapper itemMapper;

  private final static Logger log = LoggerFactory.getLogger(CartServiceImpl.class);
  private final ProductRepository productRepository;

  public CartServiceImpl(CartRepository cartRepository, ItemRepository itemRepository, CustomerRepository customerRepository, CartMapper cartMapper, ItemMapper itemMapper, ProductRepository productRepository) {
    this.cartRepository = cartRepository;
    this.itemRepository = itemRepository;
    this.customerRepository = customerRepository;
    this.cartMapper = cartMapper;
    this.itemMapper = itemMapper;
    this.productRepository = productRepository;
  }

  @Override
  @Transactional
  public Cart addItemToCart(UUID customerId, Item item) {
    // --- VALIDATION ---
    // customerId is validated by getCartEntityByCustomerId
    if (item == null) {
      throw new IllegalArgumentException("Item cannot be null.");
    }
    if (item.getProductId() == null) {
      throw new IllegalArgumentException("ProductId cannot be null.");
    }
    // --- END VALIDATION ---

    // Retrieve existing cart
    CartEntity originalCartEntity = getCartEntityByCustomerId(customerId);

    // Check that item (productId) does  not already exist as an item in cart
    long count = originalCartEntity.getItems().stream()
        .filter(i -> i.getProduct().getId().equals(item.getProductId())).count();
    if (count > 0) {
      throw new ItemAlreadyExistsException(
          String.format("Item with Id (%s) already exists.", item.getProductId()));
    }

    ItemEntity itemEntity = itemMapper.modelToEntity(item);
    itemRepository.save(itemEntity);
    originalCartEntity.getItems().add(itemEntity);
    CartEntity savedCartEntity = cartRepository.save(originalCartEntity);
    return cartMapper.entityToModel(savedCartEntity);
  }

  @Override
  @Transactional
  public Cart replaceItemInCart(UUID customerId, Item itemToUpdate) {

    // Validate input arguments
    if (itemToUpdate == null) {
      throw new IllegalArgumentException("Item cannot be null.");
    }
    if (itemToUpdate.getProductId() == null) {
      throw new IllegalArgumentException("ProductId cannot be null.");
    }

    // Get the existing cart entity
    // customerId validation is handled by getCartEntityByCustomerId
    CartEntity originalCartEntity = getCartEntityByCustomerId(customerId);

    // Initialize items list, and check there are items
    List<ItemEntity> originalItems = originalCartEntity.getItems();

    if (originalItems == null) {
      originalItems = new ArrayList<>();
      originalCartEntity.setItems(originalItems); // Set it back to the entity
    }

    // Initialize product and itemMatched
    UUID productId = itemToUpdate.getProductId();

    boolean itemMatched = false;

    // All validations have been done above so no need for validation here
    // Iterate and update existing item
    for (ItemEntity i : originalItems) {
      // Safely compare the product IDs (UUID objects)
      if (Objects.nonNull(i.getProduct()) && productId.equals(i.getProduct().getId())) {

        // Update quantity and price
        i.setQuantity(itemToUpdate.getQuantity());
        i.setPrice(new BigDecimal(itemToUpdate.getUnitPrice()));

        itemMatched = true;
        break; // Exit loop once found and updated
      }
    }

    // Add new item if no existing item was matched
    if (!itemMatched) {
      ItemEntity newItemEntity = itemMapper.modelToEntity(itemToUpdate);
      newItemEntity = itemRepository.save(newItemEntity);
      originalItems.add(newItemEntity);
    }

    // Save and return the updated list
    CartEntity savedCartEntity = cartRepository.save(originalCartEntity);
    return cartMapper.entityToModel(savedCartEntity);
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Cart> getCartByCustomerId(UUID customerId) {
    // --- VALIDATION ---
    // This method doesn't use the helper, so it needs its own check
    if (customerId == null) {
      throw new IllegalArgumentException("CustomerId cannot be null.");
    }
    // --- END VALIDATION ---
    log.info("---> getCartByCustomerId: Getting cart by customer id {}", customerId);

    // 1. Get the entity directly from the repository, which returns Optional<CartEntity>
    return cartRepository.findByCustomerId(customerId)
        // 2. Map the Optional<CartEntity> to Optional<Cart>
        .map(cartMapper::entityToModel);
  }

  // Helper method
  private CartEntity getCartEntityByCustomerId(UUID customerId) {
    // --- VALIDATION of customerID ---
    if (customerId == null) {
      throw new IllegalArgumentException("CustomerId cannot be null.");
    }

    log.info("---> getCartEntityByCustomerId: Getting cart entity by customer id {}", customerId);

    // Validate if customer exists
    CustomerEntity customerEntity = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(String.format(" - %s", customerId)));
    // --- END VALIDATION ---

    log.info("---> getCartEntityByCustomerId: Fetching cart for customerId: {}", customerId);

    // Fetch existing cart or create new cart if not exists
    CartEntity entity = cartRepository.findCartAndItemsAndProductsByCustomerId(customerId)
        .orElseGet(() -> {
          // --- LOGIC TO CREATE NEW CART ---
          log.info("---> Creating new CartEntity for customer ID: {}", customerId);

          // a. Create a new CartEntity
          CartEntity newCart = new CartEntity();

          // b. Link the customer
          newCart.setCustomer(customerEntity);

          // c. Initialize the items list (Crucial for later stream operations!)
          newCart.setItems(new ArrayList<>());

          // d. Save the new cart immediately to get an ID and persist it
          //    (This is necessary for Hibernate to manage the relationship)
          return cartRepository.save(newCart);
        });
    log.info("---> getCartEntityByCustomerId: Cart found with id: {}", entity);

    return entity;
  }

  @Transactional(readOnly = true)
  @Override
  public List<Item> getCartItemsByCustomerId(UUID customerId) {
    // customerId is validated by getCartEntityByCustomerId

    CartEntity entity = getCartEntityByCustomerId(customerId);
    return itemMapper.entityToModelList(entity.getItems());
  }

  @Transactional(readOnly = true)
  @Override
  public Item getCartItemByProductId(UUID customerId, UUID productId) {
    // --- VALIDATION ---
    // customerId is validated by getCartEntityByCustomerId
    if (productId == null) {
      throw new IllegalArgumentException("ProductId cannot be null.");
    }
    // --- END VALIDATION ---

    log.info("---> getCartItemByProductId: Fetching cart entity for customerId: {}", customerId);
    CartEntity entity = getCartEntityByCustomerId(customerId);
    log.info("---> getCartItemByProductId: Cart entity found: {}", entity);

    ItemEntity itemEntity = entity.getItems().stream()
        // 1. Filter out items where the Product is null
        .filter(i -> i.getProduct() != null)
        // 2. Filter for the matching Product ID
        .filter(i -> productId.equals(i.getProduct().getId()))
        // 3. Get the first match, or empty
        .findFirst()
        // 4. Throw custom exception if not found
        .orElseThrow(() -> new ItemNotFoundException(String.format(" for Customer ID: %s and Product ID: %s", customerId, productId)));

    return itemMapper.entityToModel(itemEntity);
  }

  @Override
  @Transactional
  public void deleteCartByCustomerId(UUID customerId) {
    // customerId is validated by getCartEntityByCustomerId

    CartEntity entity = getCartEntityByCustomerId(customerId);
    cartRepository.deleteById(entity.getId());
  }

  @Override
  @Transactional
  public void deleteItemFromCartByCustomerIdAndProductId(UUID customerId, UUID productId) {
    // --- VALIDATION ---
    // customerId is validated by getCartEntityByCustomerId
    if (productId == null) {
      throw new IllegalArgumentException("ProductId cannot be null.");
    }
    // --- END VALIDATION ---

    CartEntity entity = getCartEntityByCustomerId(customerId);

    List<ItemEntity> updatedItems = entity.getItems().stream()
        .filter(i -> !i.getProduct().getId().equals(productId))
        .collect(Collectors.toCollection(ArrayList::new)); // To guarantee mutable list

    entity.setItems(updatedItems);
    cartRepository.save(entity);
  }
}