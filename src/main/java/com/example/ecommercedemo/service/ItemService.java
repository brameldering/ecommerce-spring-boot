package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.ItemEntity;
import com.example.ecommercedemo.model.Item;

import java.util.List;

public interface ItemService {
  ItemEntity toEntity(Item m);
  List<ItemEntity> toEntityList(List<Item> items);
  Item toModel(ItemEntity e);
  List<Item> toModelList(List<ItemEntity> items);
}
