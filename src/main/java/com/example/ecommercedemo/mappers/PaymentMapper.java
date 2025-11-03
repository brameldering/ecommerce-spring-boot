package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.PaymentEntity;
import com.example.ecommercedemo.model.Payment;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentMapper {
  // Transform from entity to model
  public Payment entityToModel(PaymentEntity entity) {
    Payment resource = new Payment();

    // Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId());
    return resource;
  }

  // Transform from entity list to model list
  public List<Payment> entityToModelList(List<PaymentEntity> entities) {
    return entities.stream().map(this::entityToModel).toList();
  }
}
