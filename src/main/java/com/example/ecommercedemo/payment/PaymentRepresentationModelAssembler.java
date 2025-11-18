package com.example.ecommercedemo.payment;

import com.example.ecommercedemo.model.Payment;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<Payment, Payment> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport}
   * using the given controller class and resource type.
   */
  public PaymentRepresentationModelAssembler() {
    super(PaymentController.class, Payment.class);
  }

  /**
   * Converts the resource to HATEOAS resource
   *
   * @param resource
   */
  @Override
  public Payment toModel(Payment resource) {

    // Add HATEOAS links
    resource.add(linkTo(methodOn(PaymentController.class).getOrdersPaymentAuthorization(resource.getId())).withSelfRel());

    return resource;
  }

  /**
   * Converts the collection of Product entities to list of resources.
   *
   * @param resources
   */
  public List<Payment> toModelList(List<Payment> resources) {
    return resources.stream().map(this::toModel).toList();
  }

}
