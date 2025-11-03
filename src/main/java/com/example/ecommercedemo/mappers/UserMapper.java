package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
  // Transform from entity to model
  public User entityToModel(UserEntity entity) {
    User resource = new User();

    // Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId());
    return resource;
  }

  // Transform from entity list to model list
  public List<User> entityToModelList(List<UserEntity> entities) {
    return entities.stream().map(this::entityToModel).toList();
  }
}
