package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controller.*;
import com.example.ecommercedemo.model.Item;
import com.example.ecommercedemo.model.Order;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

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

    // 1. Add self link to the Order
    resource.add(linkTo(methodOn(OrderController.class)
        .getByOrderId(resource.getId()))
        .withSelfRel());

    // 2. Add self link to the nested Customer object
    if (Objects.nonNull(resource.getCustomer())) {
      resource.getCustomer().add(linkTo(methodOn(CustomerController.class)
          .getCustomerById(resource.getCustomer().getId()))
          .withSelfRel());
    }

    // 3. Add self link to the nested Address object
    if (Objects.nonNull(resource.getAddress())) {
      resource.getAddress().add(linkTo(methodOn(AddressController.class)
          .getAddressById(resource.getAddress().getId()))
          .withSelfRel());
    }

    // 4. Add self link to the nested Card object
    if (Objects.nonNull(resource.getCard())) {
      resource.getCard().add(linkTo(methodOn(CardController.class)
          .getCardById(resource.getCard().getId()))
          .withSelfRel());
    }

    // 5. Add self link to the nested Payment object
//    if (Objects.nonNull(resource.getPayment())) {
//      resource.getPayment().add(linkTo(methodOn(PaymentController.class)
//          .get(resource.getPayment().getId())) // Assumes getByPaymentId(UUID)
//          .withSelfRel());
//    }

    // 6. Add self link to the nested Shipment object
    if (Objects.nonNull(resource.getShipment())) {
      resource.getShipment().add(linkTo(methodOn(ShipmentController.class)
          .getShipmentByOrderId(resource.getId()))
          .withSelfRel());
    }

    // 7. Add links to each item in the list
    if (Objects.nonNull(resource.getItems())) {
      for (Item item : resource.getItems()) {
        // Add a link to the related product resource
        item.add(linkTo(methodOn(ProductController.class)
            .getProductById(item.getProductId()))
            .withRel("product"));
      }
    }

    return resource;
  }

  /**
   * Converts the collection of Order models to list of HATEOAS resources.
   *
   * @param resources
   */
  public List<Order> toModelList(List<Order> resources) {
    return resources.stream().map(this::toModel).toList();
  }

}
