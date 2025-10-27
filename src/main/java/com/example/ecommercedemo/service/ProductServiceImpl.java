package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.ProductEntity;
import com.example.ecommercedemo.hateoas.ProductRepresentationModelAssembler;
import com.example.ecommercedemo.model.Product;
import com.example.ecommercedemo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

  private final ProductRepository repository;
  private final ProductRepresentationModelAssembler assembler;

  public ProductServiceImpl(ProductRepository repository,
                            ProductRepresentationModelAssembler assembler) {
    this.repository = repository;
    this.assembler = assembler;
  }

  @Transactional(readOnly = true)
  @Override
  public List<ProductEntity> getAllProducts() {
    return repository.findAllWithTags();
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<ProductEntity> getProductEntity(String id) {
    return repository.findByIdWithTags(UUID.fromString(id));
  }

  @Transactional(readOnly = true)
  @Override
  public List<Product> getAllProductsModels() {
    List<ProductEntity> entities = repository.findAllWithTags();
    return entities.stream().map(assembler::toModel).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Product> getProductModel(String id) {
    return repository.findByIdWithTags(UUID.fromString(id)).map(assembler::toModel);
  }
}
