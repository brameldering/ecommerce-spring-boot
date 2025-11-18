package com.example.ecommercedemo.product;

import com.example.ecommercedemo.api.ProductApi;
import com.example.ecommercedemo.model.Product;
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

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @Override
  public ResponseEntity<List<Product>> queryProducts(String tag, String name, Integer page, Integer size) {
    return ok(productService.getAllProducts());
  }

  @Override
  public ResponseEntity<Product> getProductById(UUID id) {
    return productService.getProductById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }
}

