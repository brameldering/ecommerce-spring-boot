package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.ProductEntity;
import com.example.ecommercedemo.model.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Validated
public interface ProductService {
  List<ProductEntity> getAllProducts();          // internal use can keep entity return if mapping happens inside impl
  Optional<ProductEntity> getProductEntity(String id); // keep entity fetch helper if needed
  List<Product> getAllProductsModels();         // new: returns assembled API models
  Optional<Product> getProductModel(String id);
}