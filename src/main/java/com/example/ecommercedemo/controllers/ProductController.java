package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.ProductApi;
import com.example.ecommercedemo.model.Product;
import com.example.ecommercedemo.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class ProductController implements ProductApi {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<Product> getProduct(String id) {
    return service.getProductModel(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<List<Product>> queryProducts(String tag, String name, Integer page, Integer size) {
    return ok(service.getAllProductsModels());
  }
}

