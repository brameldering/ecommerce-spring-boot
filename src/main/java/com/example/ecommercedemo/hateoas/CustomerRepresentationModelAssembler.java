package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controller.*;
import com.example.ecommercedemo.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CustomerRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<Customer, Customer> {

  private final static Logger logger = LoggerFactory.getLogger(CustomerRepresentationModelAssembler.class);

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport}
   * using the given controller class and resource type.
   */
  public CustomerRepresentationModelAssembler() {
    super(CustomerController.class, Customer.class);
  }

  /**
   * Converts the resource to HATEOAS resource
   *
   * @param resource
   */
  @Override
  public Customer toModel(Customer resource) {

    logger.info("CustomerRepresentationModelAssembler toModel, for user {}", resource.getUsername());

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
  public List<Customer> toModelList(List<Customer> resources) {
    return resources.stream().map(this::toModel).toList();
  }

}