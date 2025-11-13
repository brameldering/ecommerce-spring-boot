package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controller.CartController;
import com.example.ecommercedemo.model.Cart;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CartRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<Cart, Cart> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport}
   * using the given controller class and resource type.
   */
  public CartRepresentationModelAssembler() {
    super(CartController.class, Cart.class);
  }

  /**
   * Converts the resource to HATEOAS resource
   *
   * @param resource
   */
  @Override
  public Cart toModel(Cart resource) {

// Get the customer ID; no need for the explicit ternary
    UUID uid = resource.getCustomerId();

    // --- Add HATEOAS links only if the customer ID is present ---
    if (Objects.nonNull(uid)) {
      // 1. Self Link
      // Allows the client to retrieve this specific cart using the customer ID
      resource.add(linkTo(methodOn(CartController.class).getCustomerCart(uid)).withSelfRel());

      // 2. Cart Items Link
      // Allows the client to discover the items related to this cart
      resource.add(linkTo(methodOn(CartController.class).getCustomerCartItems(uid)).withRel("cart-items"));
    }
    return resource;
  }

  /**
   * Converts the collection of Cart entities to list of resources.
   *
   * @param resources
   */
  public List<Cart> toModelList(List<Cart> resources) {
    return resources.stream().map(this::toModel).toList();
  }

}

