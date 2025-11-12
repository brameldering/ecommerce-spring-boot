package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.Cart;
import com.example.ecommercedemo.model.Item;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartService {
  Cart addItemToCart(@NotNull(message = "Customer UUID cannot be null.") UUID customerId, @Valid Item item);
  Cart replaceItemInCart(@NotNull(message = "Customer UUID cannot be null.") UUID customerId, @Valid Item item);
  Optional<Cart> getCartByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID customerId);
  List<Item> getCartItemsByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID customerId);
  Item getCartItemByProductId(@NotNull(message = "Customer UUID cannot be null.")  UUID customerId, @NotNull(message = "Item UUID cannot be null.")  UUID productId);
  void deleteCartByCustomerId(@NotNull(message = "Customer UUID cannot be null.") UUID customerId);
  void deleteItemFromCartByCustomerIdAndProductId(@NotNull(message = "Customer UUID cannot be null.")  UUID customerId, @NotNull(message = "Product UUID cannot be null.")  UUID productId);
}
