package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.CartApi;
import com.example.ecommercedemo.hateoas.CartRepresentationModelAssembler;
import com.example.ecommercedemo.hateoas.ItemRepresentationModelAssembler;
import com.example.ecommercedemo.model.Cart;
import com.example.ecommercedemo.model.Item;

import java.util.List;
import java.util.UUID;

import com.example.ecommercedemo.service.CartService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.*;

@RestController
@Validated
@RequestMapping("/api/v1")
public class CartController implements CartApi {

  private static final Logger log = LoggerFactory.getLogger(CartController.class);
  private final CartService service;

  private final CartRepresentationModelAssembler cartAssembler;
  private final ItemRepresentationModelAssembler itemAssembler;

  public CartController(CartService service, CartRepresentationModelAssembler cartAssembler, ItemRepresentationModelAssembler itemAssembler) {
    this.service = service;
    this.cartAssembler = cartAssembler;
    this.itemAssembler = itemAssembler;
  }

  @Override
  public ResponseEntity<Cart> addItemToCart(UUID customerId, @Valid @RequestBody Item item) {
    log.info("Add Item to Cart Request for customer ID: {}", customerId);

    Cart cart = service.addItemToCart(customerId, item);

    // Use the itemAssembler to add HATEOASlinks to every item in the cart
    List<Item> cartItemsWithLinks = itemAssembler.toModelList(cart.getItems(), customerId);
    cart.setItems(cartItemsWithLinks);
    return ok(cartAssembler.toModel(cart));
  }

  @Override
  public ResponseEntity<Cart> replaceItemInCart(UUID customerId, @Valid @RequestBody Item item) {

    Cart cart = service.replaceItemInCart(customerId, item);
    // Use the itemAssembler to add links to every item in the cart
    List<Item> cartItemsWithLinks = itemAssembler.toModelList(cart.getItems(), customerId);
    cart.setItems(cartItemsWithLinks);
    return ok(cartAssembler.toModel(cart));

  }

  @Override
  public ResponseEntity<Cart> getCustomerCart(UUID customerId) {

    // 1. Fetch the cart
    return service.getCartByCustomerId(customerId)
        .map(cart -> {
          // 2. Add HATEOAS links to items in cart
          List<Item> itemsWithLinks = itemAssembler.toModelList(cart.getItems(), customerId);
          cart.setItems(itemsWithLinks); // Replace original items with items with links
          return cart;
        })
        // 3. Add HATEOAS links to the cart
        .map(cartAssembler::toModel)
        .map(ResponseEntity::ok)
        .orElse(notFound().build());
  }

  @Override
  public ResponseEntity<List<Item>> getCustomerCartItems(UUID customerId) {

    List<Item> items = service.getCartItemsByCustomerId(customerId);
    List<Item> cartItemsWithLinks = items.stream()
        .map(i -> itemAssembler.toModel(i, customerId))
        .toList();
    return ok(cartItemsWithLinks);
  }

  @Override
  public ResponseEntity<Item> getCustomerCartItemByProductId(UUID customerId, UUID productId) {

    Item item = service.getCartItemByProductId(customerId, productId);
    if (item == null) {
      return notFound().build();
    }

    Item cartItemWithLinks = itemAssembler.toModel(item, customerId);
    return ok(cartItemWithLinks);
  }

  @Override
  public ResponseEntity<Void> deleteCustomerCart(UUID customerId) {

    service.deleteCartByCustomerId(customerId);
    return accepted().build();
  }

  @Override
  public ResponseEntity<Void> deleteItemFromCustomerCart(UUID customerId, UUID productId) {

    service.deleteItemFromCartByCustomerIdAndProductId(customerId, productId);
    return accepted().build();
  }
}