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
import java.util.stream.Collectors;

@Service
@Validated
public class ProductServiceImpl implements ProductService {

  private final ProductRepository repository;
  private final ProductMapper mapper;

  public ProductServiceImpl(ProductRepository repository,
                            ProductRepresentationModelAssembler assembler, ProductMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Transactional(readOnly = true)
  @Override
  public List<Product> getAllProducts() {
    List<ProductEntity> entities = repository.findAllWithTags();
    return entities.stream().map(mapper::entityToModel).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Product> getProduct(UUID id) {
    return repository.findByIdWithTags(id).map(mapper::entityToModel);
  }
}
