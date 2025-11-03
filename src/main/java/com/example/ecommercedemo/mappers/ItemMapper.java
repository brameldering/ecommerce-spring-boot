package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.ItemEntity;
import com.example.ecommercedemo.entity.ProductEntity;
import com.example.ecommercedemo.model.Item;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ItemMapper {

  public ItemEntity modelToEntity(Item item) {
    ItemEntity itemEntity = new ItemEntity();
    itemEntity.setProduct(new ProductEntity().setId(item.getId()))
        // Convert the getUnitPrice string from Item to a BigDecimal to be used for the ItemEntity
        .setPrice(new BigDecimal(item.getUnitPrice()))
        .setQuantity(item.getQuantity());
    return itemEntity;
  }

 public List<ItemEntity> modelToEntityList(List<Item> items) {
    if (Objects.isNull(items)) {
      return List.of();
    }
    return items.stream().map(this::modelToEntity).toList();
  }

  public Item entityToModel(ItemEntity entity) {
    Item resource = new Item();
    // Convert price from BigDecimal to String
    resource.id(entity.getProduct().getId()).unitPrice(entity.getPrice().toString()).quantity(entity.getQuantity());
    return resource;
  }

  public List<Item> entityToModelList(List<ItemEntity> entities) {
    return Optional.ofNullable(entities)
        .orElseGet(Collections::emptyList)
        .stream()
        .map(this::entityToModel)
        .toList();
  }
}
