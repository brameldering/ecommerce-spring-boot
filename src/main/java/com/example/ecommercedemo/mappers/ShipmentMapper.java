package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.ShipmentEntity;
import com.example.ecommercedemo.model.Shipment;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShipmentMapper {

  // Transform from entity to model
  public Shipment entityToModel(ShipmentEntity entity) {
    Shipment resource = new Shipment();

    // Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId());
    return resource;
  }

  // Transform from entity list to model list
  public List<Shipment> entityToModelList(List<ShipmentEntity> entities) {
    return entities.stream().map(this::entityToModel).toList();
  }
}
