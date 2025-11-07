package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.CartEntity;
import com.example.ecommercedemo.model.Cart;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class CartMapper {
  private final ItemMapper itemMapper;

  public CartMapper(ItemMapper itemMapper) {
    this.itemMapper = itemMapper;
  }

  public Cart entityToModel(CartEntity entity) {
    Cart resource = new Cart();

    // Retrieve user id and cart id
    UUID uid = Objects.nonNull(entity.getUser()) ? entity.getUser().getId() : null;
    UUID cid = Objects.nonNull(entity.getId()) ? entity.getId() : null;

    // Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.id(cid).customerId(uid).items(itemMapper.entityToModelList(entity.getItems()));

    return resource;
  }

}
