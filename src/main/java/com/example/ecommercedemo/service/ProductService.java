package com.example.ecommercedemo.service;

import com.example.ecommercedemo.model.Product;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Validated
public interface ProductService {
//  List<Product> getAllProducts();          // internal use can keep entity return if mapping happens inside impl
//  Optional<Product> getProductEntity(String id); // keep entity fetch helper if needed
  List<Product> getAllProductsModels();         // new: returns assembled API models
  Optional<Product> getProductModel(String id);
}