package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.CartEntity;
import com.example.ecommercedemo.model.Cart;
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
    if (entity == null) {
      return null;
    }

    Cart resource = new Cart();

    // Retrieve customer id and cart id
    UUID customerId = Objects.nonNull(entity.getCustomer()) ? entity.getCustomer().getId() : null;
    UUID cartId = Objects.nonNull(entity.getId()) ? entity.getId() : null;

    // Copy properties and set ID
    //  BeanUtils.copyProperties(entity, resource);
    // Explicitly set all DTO properties for clarity and safety,
    // replacing BeanUtils.copyProperties
    resource.id(cartId)
        .customerId(customerId)
        .items(itemMapper.entityToModelList(entity.getItems()));

    return resource;
  }
}
