package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.CardEntity;
import com.example.ecommercedemo.model.Card;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class CardMapper {

//  private static final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);

  // Transform from entity to model
  public Card entityToModel(CardEntity entity) {
    if (entity == null) {
      return null;
    }

    Card resource = new Card();

    UUID customerUuid = Objects.nonNull(entity.getCustomer()) ? entity.getCustomer().getId() : null;

    // Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
//    logger.info("Cardmodel after copyProperties " + resource);
    resource.id(entity.getId()).cardNumber(entity.getNumber())
        .cvv(entity.getCvv()).expires(entity.getExpires()).customerId(customerUuid);
//    logger.info("Cardmodel after explicit property settings " + resource);
    return resource;
  }

  // Transform from entity list to model list
  public List<Card> entityToModelList(List<CardEntity> entities) {
    return entities.stream().map(this::entityToModel).toList();
  }
}
