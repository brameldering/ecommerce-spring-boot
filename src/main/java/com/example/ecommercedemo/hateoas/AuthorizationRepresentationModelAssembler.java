package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.PaymentController;
import com.example.ecommercedemo.entity.AuthorizationEntity;
import com.example.ecommercedemo.model.Authorization;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AuthorizationRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<AuthorizationEntity, Authorization> {

  public AuthorizationRepresentationModelAssembler() {
    super(PaymentController.class, Authorization.class);
  }

  @Override
  public Authorization toModel(AuthorizationEntity entity) {
    //    Authorization resource = createModelWithId(entity.getId(), entity);
    // 1. Manually instantiate the model instead of using createModelWithId
    Authorization resource = new Authorization();

    // 2. Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setOrderId(entity.getId().toString());

    // 3. Add HATEAOS links
    resource.add(linkTo(methodOn(PaymentController.class).getOrdersPaymentAuthorization(entity.getId().toString())).withSelfRel());

    return resource;
  }

  public List<Authorization> toListModel(List<AuthorizationEntity> entities) {
    return entities.stream().map(this::toModel).collect(toList());
  }
}
