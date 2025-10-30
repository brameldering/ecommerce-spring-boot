package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.ShipmentController;
import com.example.ecommercedemo.entity.ShipmentEntity;
import com.example.ecommercedemo.model.Shipment;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ShipmentRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<ShipmentEntity, Shipment> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and
   * resource type.
   */
  public ShipmentRepresentationModelAssembler() {
    super(ShipmentController.class, Shipment.class);
  }

  /**
   * Coverts the Shipment entity to resource
   * @param entity
   * @return
   */
  @Override
  public Shipment toModel(ShipmentEntity entity) {

    //    Shipment resource = createModelWithId(entity.getId(), entity);
    // 1. Manually instantiate the model instead of using createModelWithId
    Shipment resource = new Shipment();

    // 2. Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId().toString());

    // 3. Add HATEAOS links
    resource.add(linkTo(methodOn(ShipmentController.class).getShipmentByOrderId(entity.getId().toString())).withRel("byOrderId"));

    return resource;
  }

  /**
   * Coverts the collection of Product entities to list of resources.
   * @param entities
   * @return
   */
  public List<Shipment> toListModel(List<ShipmentEntity> entities) {
    return entities.stream().map(this::toModel).collect(toList());
  }

}

