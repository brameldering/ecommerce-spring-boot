package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.CustomerEntity;
import com.example.ecommercedemo.model.Customer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMapper {
  // Transform from entity to model
  public Customer entityToModel(CustomerEntity entity) {
    if (entity == null) {
      return null;
    }

    Customer resource = new Customer();

    // Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId());
    return resource;
  }

  // Transform from entity list to model list
  public List<Customer> entityToModelList(List<CustomerEntity> entities) {
    return entities.stream().map(this::entityToModel).toList();
  }
}
