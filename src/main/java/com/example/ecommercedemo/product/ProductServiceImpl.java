package com.example.ecommercedemo.product;

import com.example.ecommercedemo.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  public ProductServiceImpl(ProductRepository productRepository,
                            ProductRepresentationModelAssembler assembler, ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.productMapper = productMapper;
  }

  @Transactional(readOnly = true)
  @Override
  public List<Product> getAllProducts() {
    List<ProductEntity> entities = productRepository.findAllWithTags();
    LOG.info("Products: ");
    List<Product> products = productMapper.entityToModelList(entities);
    LOG.info(products.toString());
    return products;
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Product> getProductById(UUID id) {
    return productRepository.findByIdWithTags(id).map(productMapper::entityToModel);
  }
}
