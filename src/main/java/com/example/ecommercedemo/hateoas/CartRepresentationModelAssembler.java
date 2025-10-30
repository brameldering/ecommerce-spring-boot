package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.CartController;
import com.example.ecommercedemo.entity.CartEntity;
import com.example.ecommercedemo.model.Cart;
import com.example.ecommercedemo.service.ItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CartRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<CartEntity, Cart> {

  private final ItemService itemService;

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and
   * resource type.
   */
  public CartRepresentationModelAssembler(ItemService itemService) {
    super(CartController.class, Cart.class);
    this.itemService = itemService;
  }

  /**
   * Coverts the Card entity to resource
   *
   * @param entity
   */
  @Override
  public Cart toModel(CartEntity entity) {
    String uid = Objects.nonNull(entity.getUser()) ? entity.getUser().getId().toString() : null;
    String cid = Objects.nonNull(entity.getId()) ? entity.getId().toString() : null;

    // 1. Manually instantiate the model instead of using createModelWithId
    Cart resource = new Cart();

    // 2. Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.id(cid).customerId(uid).items(itemService.toModelList(entity.getItems()));

    // 3. Add HATEAOS links
    resource.add(linkTo(methodOn(CartController.class).getCartByCustomerId(uid)).withSelfRel());
    resource.add(linkTo(methodOn(CartController.class).getCartItemsByCustomerId(uid)).withRel("cart-items"));

    return resource;
  }

  /**
   * Coverts the collection of Product entities to list of resources.
   *
   * @param entities
   */
  public List<Cart> toListModel(List<CartEntity> entities) {
    return entities.stream().map(this::toModel).collect(toList());
  }

}

