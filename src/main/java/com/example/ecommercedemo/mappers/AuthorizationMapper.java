package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.AuthorizationEntity;
import com.example.ecommercedemo.model.Authorization;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorizationMapper {
  // Transform from entity to model
  public Authorization entityToModel(AuthorizationEntity entity) {
    Authorization resource = new Authorization();

    // Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setOrderId(entity.getId());
    return resource;
  }

  // Transform from entity list to model list
  public List<Authorization> entityToModelList(List<AuthorizationEntity> entities) {
    return entities.stream().map(this::entityToModel).toList();
  }
}
