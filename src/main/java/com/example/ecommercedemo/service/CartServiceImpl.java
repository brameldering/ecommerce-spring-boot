package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.CartEntity;
import com.example.ecommercedemo.entity.ItemEntity;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.exceptions.GenericAlreadyExistsException;
import com.example.ecommercedemo.exceptions.ItemNotFoundException;
import com.example.ecommercedemo.mappers.CartMapper;
import com.example.ecommercedemo.mappers.ItemMapper;
import com.example.ecommercedemo.model.Cart;
import com.example.ecommercedemo.repository.CartRepository;
import com.example.ecommercedemo.repository.UserRepository;
import com.example.ecommercedemo.model.Item;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.validation.annotation.Validated;

import static org.springframework.objenesis.instantiator.util.UnsafeUtils.getUnsafe;

@Service
@Validated
public class CartServiceImpl implements CartService {

  private final CartRepository repository;
  private final UserRepository userRepo;
  private final CartMapper cartMapper;
  private final ItemMapper mapper;

  public CartServiceImpl(CartRepository repository, UserRepository userRepo, CartMapper cartMapper, ItemMapper mapper) {
    this.repository = repository;
    this.userRepo = userRepo;
    this.cartMapper = cartMapper;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public List<Item> addCartItemsByCustomerId(UUID customerId, Item item) {
    // --- VALIDATION ---
    // customerId is validated by getCartEntityByCustomerId
    if (item == null) {
      throw new IllegalArgumentException("Item cannot be null.");
    }
    if (item.getProductId() == null) {
      throw new IllegalArgumentException("ProductId cannot be null.");
    }
    // --- END VALIDATION ---

    CartEntity entity = getCartEntityByCustomerId(customerId);
    long count = entity.getItems().stream()
        .filter(i -> i.getProduct().getId().equals(item.getProductId())).count();
    if (count > 0) {
      throw new GenericAlreadyExistsException(
          String.format("Item with Id (%s) already exists. You can update it.", item.getProductId()));
    }
    entity.getItems().add(mapper.modelToEntity(item));
    return mapper.entityToModelList(repository.save(entity).getItems());
  }

  @Override
  @Transactional
  public List<Item> addOrReplaceItemsByCustomerId(UUID customerId, Item item) {

    // Validate input arguments
    if (item == null) {
      throw new IllegalArgumentException("Item cannot be null.");
    }
    if (item.getProductId() == null) {
      throw new IllegalArgumentException("ProductId cannot be null.");
    }

    // Get the existing cart entity
    // customerId validation is handled by getCartEntityByCustomerId
    CartEntity entity = getCartEntityByCustomerId(customerId);

    // Initialize items list, ensuring it's mutable if it was null
    List<ItemEntity> items = entity.getItems();
    if (items == null) {
      items = new ArrayList<>();
      entity.setItems(items); // Set the new list back to the entity
    }

    // Initialize product and itemMatched
    UUID productId = item.getProductId();
    boolean itemMatched = false;

    // All validations have been done above so no need for validation here
    // Iterate and update existing item
    for (ItemEntity i : items) {
      // Safely compare the product IDs (UUID objects)
      if (Objects.nonNull(i.getProduct()) && productId.equals(i.getProduct().getId())) {

        // Convert DTO String price to Entity BigDecimal price
        BigDecimal newPrice = new BigDecimal(item.getUnitPrice());

        // Update quantity and price
        i.setQuantity(item.getQuantity());
        i.setPrice(newPrice);

        itemMatched = true;
        break; // Exit loop once found and updated
      }
    }

    // Add new item if no existing item was matched
    if (!itemMatched) {
      items.add(mapper.modelToEntity(item));
    }

    // Save and return the updated list
    return mapper.entityToModelList(repository.save(entity).getItems());
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

    // 1. Get the entity directly from the repository, which returns Optional<CartEntity>
    return repository.findByCustomerId(customerId)
        // 2. Map the Optional<CartEntity> to Optional<Cart>
        .map(cartMapper::entityToModel);
  }

  // Helper method
  private CartEntity getCartEntityByCustomerId(UUID customerId) {
    // --- VALIDATION of customerID ---
    if (customerId == null) {
      throw new IllegalArgumentException("CustomerId cannot be null.");
    }
    // --- END VALIDATION ---

    CartEntity entity = repository.findByCustomerId(customerId)
        .orElse(new CartEntity());
    if (entity.getUser() == null) {
      entity.setUser(userRepo.findById(customerId)
          .orElseThrow(() -> new CustomerNotFoundException(
              String.format(" - %s", customerId))));
    }
    return entity;
  }

  @Transactional(readOnly = true)
  @Override
  public List<Item> getCartItemsByCustomerId(UUID customerId) {
    // customerId is validated by getCartEntityByCustomerId
    CartEntity entity = getCartEntityByCustomerId(customerId);
    return mapper.entityToModelList(entity.getItems());
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

    CartEntity entity = getCartEntityByCustomerId(customerId);
    AtomicReference<ItemEntity> itemEntity = new AtomicReference<>();
    entity.getItems().forEach(i -> {
      if (i.getProduct().getId().equals(productId)) {
        itemEntity.set(i);
      }
    });
    if (itemEntity.get() == null) {
      getUnsafe().throwException(new ItemNotFoundException(String.format(" - %s", productId)));
    }
    return mapper.entityToModel(itemEntity.get());
  }

  @Override
  @Transactional
  public void deleteCart(UUID customerId) {
    // customerId is validated by getCartEntityByCustomerId
    // will throw the error if it doesn't exist
    CartEntity entity = getCartEntityByCustomerId(customerId);
    repository.deleteById(entity.getId());
  }

  @Override
  @Transactional
  public void deleteItemFromCart(UUID customerId, UUID itemId) {
    // --- VALIDATION ---
    // customerId is validated by getCartEntityByCustomerId
    if (itemId == null) {
      throw new IllegalArgumentException("ItemId cannot be null.");
    }
    // --- END VALIDATION ---

    CartEntity entity = getCartEntityByCustomerId(customerId);
    List<ItemEntity> updatedItems = entity.getItems().stream()
        .filter(i -> !i.getProduct().getId().equals(itemId)).toList();
    entity.setItems(updatedItems);
    repository.save(entity);
  }
}