package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.ProductEntity;
import com.example.ecommercedemo.hateoas.ProductRepresentationModelAssembler;
import com.example.ecommercedemo.mappers.ProductMapper;
import com.example.ecommercedemo.model.Product;
import com.example.ecommercedemo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  public ProductServiceImpl(ProductRepository productRepository,
                            ProductRepresentationModelAssembler assembler, ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.productMapper = productMapper;
  }

  @Transactional(readOnly = true)
  @Override
  public List<Product> getAllProducts() {
    List<ProductEntity> entities = productRepository.findAllWithTags();
    return productMapper.entityToModelList(entities);
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Product> getProductById(UUID id) {
    return productRepository.findByIdWithTags(id).map(productMapper::entityToModel);
  }
}
