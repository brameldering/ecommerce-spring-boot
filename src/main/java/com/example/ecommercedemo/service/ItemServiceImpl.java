package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.ItemEntity;
import com.example.ecommercedemo.entity.ProductEntity;
import com.example.ecommercedemo.model.Item;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
public class ItemServiceImpl implements ItemService {

  @Override
  public ItemEntity toEntity(Item m) {
    ItemEntity e = new ItemEntity();
    e.setProduct(new ProductEntity().setId(UUID.fromString(m.getId())))
        // Convert the getUnitPrice string from Item to a BigDecimal to be used for the ItemEntity
        .setPrice(new BigDecimal(m.getUnitPrice()))
        .setQuantity(m.getQuantity());
    return e;
  }

  @Override
  public List<ItemEntity> toEntityList(List<Item> items) {
    if (Objects.isNull(items)) {
      return List.of();
    }
    return items.stream().map(this::toEntity).collect(toList());
  }

  @Override
  public Item toModel(ItemEntity e) {
    Item m = new Item();
    // Convert price from BigDecimal to String
    m.id(e.getProduct().getId().toString()).unitPrice(e.getPrice().toString()).quantity(e.getQuantity());
    return m;
  }

  @Override
  public List<Item> toModelList(List<ItemEntity> items) {
    if (Objects.isNull(items)) {
      return List.of();
    }
    return items.stream().map(this::toModel).collect(toList());
  }
}
