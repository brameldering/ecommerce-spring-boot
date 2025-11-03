package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.CartEntity;
import com.example.ecommercedemo.entity.ItemEntity;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.exceptions.GenericAlreadyExistsException;
import com.example.ecommercedemo.exceptions.ItemNotFoundException;
import com.example.ecommercedemo.mappers.ItemMapper;
import com.example.ecommercedemo.model.Cart;
import com.example.ecommercedemo.repository.CartRepository;
import com.example.ecommercedemo.repository.UserRepository;
import com.example.ecommercedemo.model.Item;
import org.springframework.beans.BeanUtils;
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
  private final ItemMapper mapper;

  public CartServiceImpl(CartRepository repository, UserRepository userRepo, ItemMapper mapper) {
    this.repository = repository;
    this.userRepo = userRepo;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public List<Item> addCartItemsByCustomerId(UUID customerId, Item item) {
    CartEntity entity = getCartEntityByCustomerId(customerId);
    long count = entity.getItems().stream()
        .filter(i -> i.getProduct().getId().equals(item.getId())).count();
    if (count > 0) {
      throw new GenericAlreadyExistsException(
          String.format("Item with Id (%s) already exists. You can update it.", item.getId()));
    }
    entity.getItems().add(mapper.modelToEntity(item));
    return mapper.entityToModelList(repository.save(entity).getItems());
  }

  @Override
  @Transactional
  public List<Item> addOrReplaceItemsByCustomerId(UUID customerId, Item item) {
    // 1. Get the existing cart entity
    CartEntity entity = getCartEntityByCustomerId(customerId);

    // Initialize items list, ensuring it's mutable if it was null
    List<ItemEntity> items = entity.getItems();
    if (Objects.isNull(items)) {
      items = new ArrayList<>();
      entity.setItems(items); // Set the new list back to the entity
    }

    // Check if the incoming item has a valid, non-null ID for matching
    String itemId = item.getId().toString();
    boolean itemMatched = false;

    if (itemId != null && !itemId.trim().isEmpty()) {
      try {
        // Iterate and update existing item
        for (ItemEntity i : items) {
          // Safely compare the product IDs
          if (i.getProduct() != null && i.getProduct().getId().equals(itemId)) {
            // Convert DTO String price to Entity BigDecimal price
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
      items.add(mapper.modelToEntity(item));
    }

    // 4. Save and return the updated list
    return mapper.entityToModelList(repository.save(entity).getItems());
  }

  @Override
  @Transactional
  public void deleteCart(UUID customerId) {
    // will throw the error if it doesn't exist
    CartEntity entity = getCartEntityByCustomerId(customerId);
    repository.deleteById(entity.getId());
  }

  @Override
  @Transactional
  public void deleteItemFromCart(UUID customerId, UUID itemId) {
    CartEntity entity = getCartEntityByCustomerId(customerId);
    List<ItemEntity> updatedItems = entity.getItems().stream()
        .filter(i -> !i.getProduct().getId().equals(itemId)).toList();
    entity.setItems(updatedItems);
    repository.save(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Cart> getCartByCustomerId(UUID customerId) {
    // 1. Get the entity directly from the repository, which returns Optional<CartEntity>
    return repository.findByCustomerId(customerId)
        // 2. Map the Optional<CartEntity> to Optional<Cart>
        .map(this::entityToModel);
  }

  // Helper method
  private CartEntity getCartEntityByCustomerId(UUID customerId) {
    CartEntity entity = repository.findByCustomerId(customerId)
        .orElse(new CartEntity());
    if (Objects.isNull(entity.getUser())) {
      entity.setUser(userRepo.findById(customerId)
          .orElseThrow(() -> new CustomerNotFoundException(
              String.format(" - %s", customerId))));
    }
    return entity;
  }

  @Transactional(readOnly = true)
  @Override
  public List<Item> getCartItemsByCustomerId(UUID customerId) {
    CartEntity entity = getCartEntityByCustomerId(customerId);
    return mapper.entityToModelList(entity.getItems());
  }

  @Transactional(readOnly = true)
  @Override
  public Item getCartItemsByItemId(UUID customerId, UUID itemId) {
    CartEntity entity = getCartEntityByCustomerId(customerId);
    AtomicReference<ItemEntity> itemEntity = new AtomicReference<>();
    entity.getItems().forEach(i -> {
      if (i.getProduct().getId().equals(itemId)) {
        itemEntity.set(i);
      }
    });
    if (Objects.isNull(itemEntity.get())) {
      getUnsafe().throwException(new ItemNotFoundException(String.format(" - %s", itemId)));
    }
    return mapper.entityToModel(itemEntity.get());
  }

  // Transform from entity to model
  private Cart entityToModel(CartEntity entity) {
    Cart resource = new Cart();

    // Retrieve user id and cart id
    UUID uid = Objects.nonNull(entity.getUser()) ? entity.getUser().getId() : null;
    UUID cid = Objects.nonNull(entity.getId()) ? entity.getId() : null;

    // Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.id(cid).customerId(uid).items(mapper.entityToModelList(entity.getItems()));

    return resource;
  };

  // Transform from entity list to model list
//  private List<Cart> entityToModelList(List<CartEntity> entities) {
//    return entities.stream().map(this::entityToModel).collect(toList());
//  }

}