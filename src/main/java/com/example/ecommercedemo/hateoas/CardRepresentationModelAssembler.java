package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.CardController;
import com.example.ecommercedemo.entity.CardEntity;
import com.example.ecommercedemo.model.Card;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CardRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<CardEntity, Card> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and
   * resource type.
   */
  public CardRepresentationModelAssembler() {
    super(CardController.class, Card.class);
  }

  /**
   * Coverts the Card entity to resource
   * @param entity
   * @return
   */
  @Override
  public Card toModel(CardEntity entity) {
    String uid = Objects.nonNull(entity.getUser()) ? entity.getUser().getId().toString() : null;
    //    Card resource = createModelWithId(entity.getId(), entity);
    // 1. Manually instantiate the model instead of using createModelWithId
    Card resource = new Card();

    // 2. Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.id(entity.getId().toString()).cardNumber(entity.getNumber())
        .cvv(entity.getCvv()).expires(entity.getExpires()).userId(uid);

    // 3. Add HATEAOS links
    resource.add(linkTo(methodOn(CardController.class).getCardById(entity.getId().toString())).withSelfRel());

    return resource;
  }

  /**
   * Coverts the collection of Product entities to list of resources.
   * @param entities
   * @return
   */
  public List<Card> toListModel(List<CardEntity> entities) {
    return entities.stream().map(this::toModel).collect(toList());
  }

}
