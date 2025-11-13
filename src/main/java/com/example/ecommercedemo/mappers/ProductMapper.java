package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.ProductEntity;
import com.example.ecommercedemo.model.Product;
import com.example.ecommercedemo.model.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {
  // Transform from entity to model
  public Product entityToModel(ProductEntity entity) {
    if (entity == null) {
      return null;
    }

    Product resource = new Product();

    // Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId());
    resource.setTag(
        entity.getTags().stream().map(t -> new Tag().id(t.getId()).name(t.getName())).toList());
    return resource;
  }

  // Transform from entity list to model list
  public List<Product> entityToModelList(List<ProductEntity> entities) {
    return entities.stream().map(this::entityToModel).toList();
  }
}
