package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.CartController;
import com.example.ecommercedemo.controllers.ProductController;
import com.example.ecommercedemo.model.Item;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ItemRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<Item, Item> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport}
   * using the given controller class and resource type.
   */
  public ItemRepresentationModelAssembler() {
    super(CartController.class, Item.class);
  }

  /**
   * Converts the resource to HATEOAS resource
   *
   * @param resource
   */
  public Item toModel(Item resource, UUID customerId) {

    // --- Self Link (Item within a Cart) ---
    resource.add(linkTo(methodOn(CartController.class).getCustomerCartItemByProductId(customerId, resource.getProductId()))
        .withSelfRel());

    // --- Product Link ---
    resource.add(linkTo(methodOn(ProductController.class).getProduct(resource.getProductId()))
        .withRel("product"));

    // --- Parent Cart Link (optional, but useful) ---
    resource.add(linkTo(methodOn(CartController.class).getCustomerCart(customerId))
        .withRel("cart"));
    return resource;
  }

  /**
   * Converts the collection of Cart entities to list of resources.
   *
   * @param resources
   */
  public List<Item> toModelList(List<Item> resources, UUID customerId) {
    return resources.stream().map(item -> toModel(item, customerId)).toList();
  }

  @Override
  public Item toModel(Item item) {
    throw new UnsupportedOperationException("Use toModel(Item, UUID) instead.");
  }
}

