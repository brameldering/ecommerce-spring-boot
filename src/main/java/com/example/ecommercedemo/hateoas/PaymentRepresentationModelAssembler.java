package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.PaymentController;
import com.example.ecommercedemo.entity.PaymentEntity;
import com.example.ecommercedemo.model.Payment;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<PaymentEntity, Payment> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and
   * resource type.
   */
  public PaymentRepresentationModelAssembler() {
    super(PaymentController.class, Payment.class);
  }

  /**
   * Coverts the Payment entity to resource
   *
   * @param entity
   */
  @Override
  public Payment toModel(PaymentEntity entity) {

    //    Payment resource = createModelWithId(entity.getId(), entity);
    // 1. Manually instantiate the model instead of using createModelWithId
    Payment resource = new Payment();

    // 2. Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId().toString());

    // 3. Add HATEAOS links
    resource.add(linkTo(methodOn(PaymentController.class).getOrdersPaymentAuthorization(entity.getId().toString())).withSelfRel());

    return resource;
  }

  /**
   * Coverts the collection of Product entities to list of resources.
   *
   * @param entities
   */
  public List<Payment> toListModel(List<PaymentEntity> entities) {
    return entities.stream().map(this::toModel).collect(toList());
  }

}
