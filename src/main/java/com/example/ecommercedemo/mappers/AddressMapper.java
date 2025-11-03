package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.AddressEntity;
import com.example.ecommercedemo.model.Address;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressMapper {
  // Transform from entity to model
  public Address entityToModel(AddressEntity entity) {
    Address resource = new Address();

    // Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId());
    return resource;
  }

  // Transform from entity list to model list
  public List<Address> entityToModelList(List<AddressEntity> entities) {
    return entities.stream().map(this::entityToModel).toList();
  }
}
