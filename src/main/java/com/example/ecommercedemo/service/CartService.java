package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.Cart;
import com.example.ecommercedemo.model.Item;
import jakarta.validation.Valid;

import java.util.List;

public interface CartService {
  List<Item> addCartItemsByCustomerId(String customerId, @Valid Item item);
  List<Item> addOrReplaceItemsByCustomerId(String customerId, @Valid Item item);
  void deleteCart(String customerId);
  void deleteItemFromCart(String customerId, String itemId);
  Cart getCartByCustomerId(String customerId);
  List<Item> getCartItemsByCustomerId(String customerId);
  Item getCartItemsByItemId(String customerId, String itemId);
}
