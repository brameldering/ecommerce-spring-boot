package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.CustomerController;
import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<UserEntity, User> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and
   * resource type.
   */
  public UserRepresentationModelAssembler() {
    super(CustomerController.class, User.class);
  }

  /**
   * Converts the User entity to resource
   *
   * @param entity
   */
  @Override
  public User toModel(UserEntity entity) {

    //    User resource = createModelWithId(entity.getId(), entity);
    // 1. Manually instantiate the model instead of using createModelWithId
    User resource = new User();

    // 2. Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId().toString());

    // 3. Add HATEAOS links
    resource.add(linkTo(methodOn(CustomerController.class).getCustomerById(entity.getId().toString())).withSelfRel());

    resource.add(linkTo(methodOn(CustomerController.class).getAllCustomers()).withRel("customers"));

    resource.add(linkTo(methodOn(CustomerController.class).getAddressesByCustomerId(entity.getId().toString())).withRel("self_addresses"));

    return resource;
  }

  /**
   * Converts the collection of Product entities to list of resources.
   *
   * @param entities
   */
  public List<User> toListModel(List<UserEntity> entities) {
    return entities.stream().map(this::toModel).collect(toList());
  }

}