package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.Cart;
import com.example.ecommercedemo.model.Item;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartService {
  List<Item> addCartItemsByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID customerId, Item item);
  List<Item> addOrReplaceItemsByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID customerId, Item item);
  void deleteCart(@NotNull(message = "Customer UUID cannot be null.") UUID customerId);
  void deleteItemFromCart(@NotNull(message = "Customer UUID cannot be null.")  UUID customerId, @NotNull(message = "Item UUID cannot be null.")  UUID itemId);
  Optional<Cart> getCartByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID customerId);
  List<Item> getCartItemsByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID customerId);
  Item getCartItemsByItemId(@NotNull(message = "Customer UUID cannot be null.")  UUID customerId, @NotNull(message = "Item UUID cannot be null.")  UUID itemId);
}
