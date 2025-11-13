package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.model.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

  // Transform from entity to model
  public Order entityToModel(OrderEntity entity) {
    if (entity == null) {
      return null;
    }

    Order resource = new Order();

    // Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId());
    // TO DO : MAP ITEMS etc
//        .customer(uAssembler.toModel(entity.getUserEntity()))
//        .address(aAssembler.toModel(entity.getAddressEntity()))
//        .card(cAssembler.toModel(entity.getCardEntity()))
//        .items(itemService.entityToModelList(entity.getItems()))
//        .date(entity.getOrderDate().toInstant().atOffset(ZoneOffset.UTC));

    return resource;
  }

  // Transform from entity list to model list
  public List<Order> entityToModelList(List<OrderEntity> entities) {
    return entities.stream().map(this::entityToModel).toList();
  }
}
