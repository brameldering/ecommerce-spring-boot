package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.*;
import com.example.ecommercedemo.model.User;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<User, User> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport}
   * using the given controller class and resource type.
   */
  public UserRepresentationModelAssembler() {
    super(CustomerController.class, User.class);
  }

  /**
   * Converts the resource to HATEOAS resource
   *
   * @param resource
   */
  @Override
  public User toModel(User resource) {

    // 3. Add HATEOAS links
    resource.add(linkTo(methodOn(CustomerController.class).getCustomerById(resource.getId())).withSelfRel());

    resource.add(linkTo(methodOn(AdminController.class).getAllCustomers()).withRel("customers"));

    resource.add(linkTo(methodOn(AddressController.class).getCustomerAddresses(resource.getId())).withRel("addresses"));

    resource.add(linkTo(methodOn(CardController.class).getCustomerCards(resource.getId())).withRel("cards"));

    resource.add(linkTo(methodOn(CartController.class).getCustomerCart(resource.getId())).withRel("cart"));

    resource.add(linkTo(methodOn(OrderController.class).getCustomerOrders(resource.getId())).withRel("orders"));

    return resource;
  }

  /**
   * Converts the collection of User entities to list of resources.
   *
   * @param resources
   */
  public List<User> toModelList(List<User> resources) {
    return resources.stream().map(this::toModel).toList();
  }

}