package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.AddressEntity;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.model.AddressReq;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressMapper {
  // Transform from entity to model
  public Address entityToModel(AddressEntity entity) {
    if (entity == null) {
      return null;
    }
    Address resource = new Address();

    // Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId());
    resource.setUserId(entity.getUser().getId());

    return resource;
  }

  // Transform from entity list to model list
  public List<Address> entityToModelList(List<AddressEntity> entities) {
    return entities.stream().map(this::entityToModel).toList();
  }

  public AddressEntity addressReqToEntity(AddressReq model) {
    if (model == null) {
      return null;
    }

    AddressEntity entity = new AddressEntity();
    return entity.setNumber(model.getNumber()).setResidency(model.getResidency())
        .setStreet(model.getStreet()).setCity(model.getCity()).setState(model.getState())
        .setCountry(model.getCountry()).setZipcode(model.getZipcode());
  }
}
