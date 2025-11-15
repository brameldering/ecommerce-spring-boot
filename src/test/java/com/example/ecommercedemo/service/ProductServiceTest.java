package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.ProductEntity;
import com.example.ecommercedemo.entity.TagEntity;
import com.example.ecommercedemo.mappers.ProductMapper;
import com.example.ecommercedemo.model.Product;
import com.example.ecommercedemo.model.Tag;
import com.example.ecommercedemo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ProductMapper productMapper;

  @InjectMocks
  private ProductServiceImpl productService;

  // --- Test Data ---
  private UUID productId;
  private UUID tagId;
  private ProductEntity productEntity;
  private TagEntity tagEntity;
  private Product productModel;
  private Tag tagModel;

  @BeforeEach
  void setUp() {
    productId = UUID.randomUUID();
    tagId = UUID.randomUUID();

    // 1. Setup Entities
    tagEntity = new TagEntity();
    tagEntity.setId(tagId);
    tagEntity.setName("tag");

    productEntity = new ProductEntity();
    productEntity.setId(productId);
    productEntity.setName("product");
    productEntity.setPrice(BigDecimal.valueOf(10));
    productEntity.setTags(List.of(tagEntity));

    // 2. Setup Models/DTOs
    tagModel = new Tag();
    tagModel.setId(tagId);
    tagModel.setName("tag");

    productModel = new Product();
    productModel.setId(productId);
    productModel.setName("product");
    productModel.setPrice("10");
    productModel.addTagItem(tagModel);
  }

  // ------------------------------------------------------------------
  // Query Methods Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("GET_ALL: Should return a list of all products")
  void getAllProducts_ReturnsList() {
    // --- Setup Mocks ---
    List<ProductEntity> entityList = List.of(productEntity);
    List<Product> modelList = List.of(productModel);
    when(productRepository.findAllWithTags()).thenReturn(entityList);
    when(productMapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    List<Product> result = productService.getAllProducts();

    // --- Assert & Verify ---
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(productRepository, times(1)).findAllWithTags();
  }

  @Test
  @DisplayName("GET_BY_ID: Should return Optional.of(Product) when found")
  void getProductById_WhenFound_ReturnsProduct() {
    // --- Setup Mocks ---
    when(productRepository.findByIdWithTags(productId)).thenReturn(Optional.of(productEntity));
    when(productMapper.entityToModel(productEntity)).thenReturn(productModel);

    // --- Execute ---
    Optional<Product> result = productService.getProductById(productId);

    // --- Assert & Verify ---
    assertTrue(result.isPresent());
    assertEquals(productId, result.get().getId());
    verify(productRepository, times(1)).findByIdWithTags(productId);
  }

  @Test
  @DisplayName("GET_BY_ID: Should return Optional.empty() when not found")
  void getAddressById_WhenNotFound_ReturnsEmptyOptional() {
    // --- Setup Mocks ---
    when(productRepository.findByIdWithTags(productId)).thenReturn(Optional.empty());

    // --- Execute ---
    Optional<Product> result = productService.getProductById(productId);

    // --- Assert & Verify ---
    assertFalse(result.isPresent());
    verify(productRepository, times(1)).findByIdWithTags(productId);
    verify(productMapper, never()).entityToModel(any());
  }
}
