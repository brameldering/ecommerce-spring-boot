package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.AddressController;
import com.example.ecommercedemo.entity.AddressEntity;
import com.example.ecommercedemo.model.Address;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AddressRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<AddressEntity, Address> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and
   * resource type.
   */
  public AddressRepresentationModelAssembler() {
    super(AddressController.class, Address.class);
  }

  /**
   * Coverts the Address entity to resource
   *
   * @param entity
   */
  @Override
  public Address toModel(AddressEntity entity) {

    //    Address resource = createModelWithId(entity.getId(), entity);
    // 1. Manually instantiate the model instead of using createModelWithId
    Address resource = new Address();

    // 2. Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId().toString());

    // 3. Add HATEAOS links
    resource.add(linkTo(methodOn(AddressController.class).getAddressesById(entity.getId().toString())).withSelfRel());

    return resource;
  }

  /**
   * Coverts the collection of Product entities to list of resources.
   *
   * @param entities
   */
  public List<Address> toListModel(List<AddressEntity> entities) {
    return entities.stream().map(this::toModel).collect(toList());
  }

}
