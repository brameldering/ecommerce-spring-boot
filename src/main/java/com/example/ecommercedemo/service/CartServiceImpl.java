package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.CartEntity;
import com.example.ecommercedemo.entity.ItemEntity;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.exceptions.GenericAlreadyExistsException;
import com.example.ecommercedemo.exceptions.ItemNotFoundException;
import com.example.ecommercedemo.model.Cart;
import com.example.ecommercedemo.repository.CartRepository;
import com.example.ecommercedemo.repository.UserRepository;
import com.example.ecommercedemo.model.Item;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import com.example.ecommercedemo.hateoas.CartRepresentationModelAssembler;

import static java.util.stream.Collectors.toList;
import static org.springframework.objenesis.instantiator.util.UnsafeUtils.getUnsafe;

@Service
public class CartServiceImpl implements CartService {

  private final CartRepository repository;
  private final UserRepository userRepo;
  private final ItemService itemService;

  private final CartRepresentationModelAssembler assembler;

  public CartServiceImpl(CartRepository repository, UserRepository userRepo, ItemService itemService, CartRepresentationModelAssembler assembler) {
    this.repository = repository;
    this.userRepo = userRepo;
    this.itemService = itemService;
    this.assembler = assembler;
  }

  @Override
  @Transactional
  public List<Item> addCartItemsByCustomerId(String customerId, @Valid Item item) {
    CartEntity entity = getCartEntityByCustomerId(customerId);
    long count = entity.getItems().stream()
        .filter(i -> i.getProduct().getId().equals(UUID.fromString(item.getId()))).count();
    if (count > 0) {
      throw new GenericAlreadyExistsException(
          String.format("Item with Id (%s) already exists. You can update it.", item.getId()));
    }
    entity.getItems().add(itemService.toEntity(item));
    return itemService.toModelList(repository.save(entity).getItems());
  }

  @Override
  @Transactional
  public List<Item> addOrReplaceItemsByCustomerId(String customerId, @Valid Item item) {
    // 1. Get the existing cart entity
    CartEntity entity = getCartEntityByCustomerId(customerId);

    // Initialize items list, ensuring it's mutable if it was null
    List<ItemEntity> items = entity.getItems();
    if (Objects.isNull(items)) {
      items = new ArrayList<>();
      entity.setItems(items); // Set the new list back to the entity
    }

    // Check if the incoming item has a valid, non-null ID for matching
    String itemId = item.getId();
    boolean itemMatched = false;

    if (itemId != null && !itemId.trim().isEmpty()) {
      try {
        // Convert the DTO string ID to UUID for comparison
        UUID itemUuid = UUID.fromString(itemId);

        // 2. Iterate and update existing item
        for (ItemEntity i : items) {
          // Safely compare the product IDs
          if (i.getProduct() != null && i.getProduct().getId().equals(itemUuid)) {
            // Fix 1: Convert DTO String price to Entity BigDecimal price
            BigDecimal newPrice = new BigDecimal(item.getUnitPrice());

            // Update quantity and price
            i.setQuantity(item.getQuantity());
            i.setPrice(newPrice);

            itemMatched = true;
            break; // Exit loop once found and updated
          }
        }
      } catch (IllegalArgumentException e) {
        // Log/handle if the ID string is not a valid UUID format
        throw new IllegalArgumentException("Invalid item ID format: " + itemId, e);
      }
    } else {
      // Decide policy: If no ID is provided, should it always be treated as a new item?
      // Assuming YES for the purpose of the fix.
    }

    // 3. Add new item if no existing item was matched
    if (!itemMatched) {
      items.add(itemService.toEntity(item));
    }

    // 4. Save and return the updated list
    return itemService.toModelList(repository.save(entity).getItems());
  }

  @Override
  @Transactional
  public void deleteCart(String customerId) {
    // will throw the error if it doesn't exist
    CartEntity entity = getCartEntityByCustomerId(customerId);
    repository.deleteById(entity.getId());
  }

  @Override
  @Transactional
  public void deleteItemFromCart(String customerId, String itemId) {
    CartEntity entity = getCartEntityByCustomerId(customerId);
    List<ItemEntity> updatedItems = entity.getItems().stream()
        .filter(i -> !i.getProduct().getId().equals(UUID.fromString(itemId))).collect(toList());
    entity.setItems(updatedItems);
    repository.save(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public Cart getCartByCustomerId(String customerId) {
    CartEntity entity = getCartEntityByCustomerId(customerId);
    return assembler.toModel(entity);
  }

  // Helper method
  private CartEntity getCartEntityByCustomerId(String customerId) {
    CartEntity entity = repository.findByCustomerId(UUID.fromString(customerId))
        .orElse(new CartEntity());
    if (Objects.isNull(entity.getUser())) {
      entity.setUser(userRepo.findById(UUID.fromString(customerId))
          .orElseThrow(() -> new CustomerNotFoundException(
              String.format(" - %s", customerId))));
    }
    return entity;
  }

  @Transactional(readOnly = true)
  @Override
  public List<Item> getCartItemsByCustomerId(String customerId) {
    CartEntity entity = getCartEntityByCustomerId(customerId);
    return itemService.toModelList(entity.getItems());
  }

  @Transactional(readOnly = true)
  @Override
  public Item getCartItemsByItemId(String customerId, String itemId) {
    CartEntity entity = getCartEntityByCustomerId(customerId);
    AtomicReference<ItemEntity> itemEntity = new AtomicReference<>();
    entity.getItems().forEach(i -> {
      if (i.getProduct().getId().equals(UUID.fromString(itemId))) {
        itemEntity.set(i);
      }
    });
    if (Objects.isNull(itemEntity.get())) {
      getUnsafe().throwException(new ItemNotFoundException(String.format(" - %s", itemId)));
    }
    return itemService.toModel(itemEntity.get());
  }
}