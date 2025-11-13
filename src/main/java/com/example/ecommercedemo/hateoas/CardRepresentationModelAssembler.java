package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controller.CardController;
import com.example.ecommercedemo.model.Card;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CardRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<Card, Card> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport}
   * using the given controller class and resource type.
   */
  public CardRepresentationModelAssembler() {
    super(CardController.class, Card.class);
  }

  /**
   * Converts the resource to HATEOAS resource
   * @param resource
   */
  @Override
  public Card toModel(Card resource) {

    // Add HATEOAS links
    resource.add(linkTo(methodOn(CardController.class).getCardById(resource.getId())).withSelfRel());

    return resource;
  }

  /**
   * Converts the collection of Card entities to list of resources.
   * @param resources
   * @return
   */
  public List<Card> toModelList(List<Card> resources) {
    return resources.stream().map(this::toModel).toList();
  }

}
