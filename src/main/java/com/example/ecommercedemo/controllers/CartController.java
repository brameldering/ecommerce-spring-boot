package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.CartApi;
import com.example.ecommercedemo.hateoas.CartRepresentationModelAssembler;
import com.example.ecommercedemo.model.Cart;
import com.example.ecommercedemo.model.Item;

import java.util.List;
import java.util.UUID;

import com.example.ecommercedemo.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.*;

@RestController
@Validated
@RequestMapping("/api/v1")
public class CartController implements CartApi {

  private static final Logger log = LoggerFactory.getLogger(CartController.class);
  private final CartService service;

  private final CartRepresentationModelAssembler assembler;

  public CartController(CartService service, CartRepresentationModelAssembler assembler) {
    this.service = service;
    this.assembler = assembler;
  }

  @Override
  public ResponseEntity<List<Item>> addCustomerCartItems(UUID customerId, Item item) {
    log.info("Request for customer ID: {}\nItem: {}", customerId, item);

    List<Item> items = service.addCartItemsByCustomerId(customerId, item);
    // TO DO: Add HATEOAS links to all carts in the list
//        List<Item> cartItemsWithLinks = assembler.toListModel(items);
//        return ok(cartItemsWithLinks);
    return ok(items);
  }

  @Override
  public ResponseEntity<List<Item>> addOrReplaceCustomerCartItems(UUID customerId, Item item) {

    // TO DO: Add HATEOAS links to all cart s in the list
    //    List<Item> cartItemsWithLinks = assembler.toListModel(items);
    return ok(service.addOrReplaceItemsByCustomerId(customerId, item));
  }

  @Override
  public ResponseEntity<Void> deleteCustomerCart(UUID customerId) {

    service.deleteCart(customerId);
    return accepted().build();
  }

  @Override
  public ResponseEntity<Void> deleteItemFromCustomerCart(UUID customerId, UUID productId) {

    service.deleteItemFromCart(customerId, productId);
    return accepted().build();
  }

  @Override
  public ResponseEntity<Cart> getCustomerCart(UUID customerId) {

    return service.getCartByCustomerId(customerId)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElse(notFound().build());
  }

  @Override
  public ResponseEntity<List<Item>> getCustomerCartItems(UUID customerId) {

    // TO DO: Add HATEOAS links to all cards in the list
//    List<Item> cartItemsWithLinks = assembler.toListModel(items);
    return ok(service.getCartItemsByCustomerId(customerId));
  }

  @Override
  public ResponseEntity<Item> getCustomerCartItemsByProductId(UUID customerId, UUID productId) {

    // TO DO: Add HATEOAS links to all cards in the list
//    List<Item> cartItemsWithLinks = assembler.toListModel(items);
    return ok(service.getCartItemsByItemId(customerId, productId));
  }
}