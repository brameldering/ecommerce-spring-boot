package com.example.ecommercedemo.controllers;

import com.example.ecommercedemo.api.ProductApi;
import com.example.ecommercedemo.model.Product;
import com.example.ecommercedemo.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/api/v1")
public class ProductController implements ProductApi {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<List<Product>> queryProducts(String tag, String name, Integer page, Integer size) {
    return ok(service.getAllProducts());
  }

  @Override
  public ResponseEntity<Product> getProduct(UUID id) {
    return service.getProduct(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }
}

