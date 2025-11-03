package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.OrderController;
import com.example.ecommercedemo.model.Order;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<Order, Order> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport}
   * using the given controller class and resource type.
   */
  public OrderRepresentationModelAssembler() {
    super(OrderController.class, Order.class);
  }

  /**
   * Converts the resource to HATEOAS resource
   *
   * @param resource
   */
  @Override
  public Order toModel(Order resource) {

    // Add HATEOAS links
    resource.add(linkTo(methodOn(OrderController.class).getByOrderId(resource.getId())).withSelfRel());

    return resource;
  }

  /**
   * Converts the collection of Order models to list of HATEOAS resources.
   *
   * @param resources
   */
  public List<Order> toListModel(List<Order> resources) {
    return resources.stream().map(this::toModel).toList();
  }

}
