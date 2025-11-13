package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controller.PaymentController;
import com.example.ecommercedemo.model.Authorization;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AuthorizationRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<Authorization, Authorization> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport}
   * using the given controller class and resource type.
   */
  public AuthorizationRepresentationModelAssembler() {
    super(PaymentController.class, Authorization.class);
  }

  @Override
  public Authorization toModel(Authorization resource) {

    // Add HATEOAS links
    resource.add(linkTo(methodOn(PaymentController.class).getOrdersPaymentAuthorization(resource.getOrderId())).withSelfRel());

    return resource;
  }

  public List<Authorization> toModelList(List<Authorization> resources) {
    return resources.stream().map(this::toModel).toList();
  }
}
