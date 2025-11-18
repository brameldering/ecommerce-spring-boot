package com.example.ecommercedemo.product;

import com.example.ecommercedemo.model.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Validated
public interface ProductService {
  List<Product> getAllProducts();         // new: returns assembled API models
  Optional<Product> getProductById(@NotNull(message = "Product UUID cannot be null.") UUID id);
}