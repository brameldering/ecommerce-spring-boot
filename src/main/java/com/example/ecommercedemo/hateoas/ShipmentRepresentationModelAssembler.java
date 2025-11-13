package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controller.ShipmentController;
import com.example.ecommercedemo.model.Shipment;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ShipmentRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<Shipment, Shipment> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport}
   * using the given controller class and resource type.
   */
  public ShipmentRepresentationModelAssembler() {
    super(ShipmentController.class, Shipment.class);
  }

  /**
   * Converts the resource to HATEOAS resource
   *
   * @param resource
   */
  @Override
  public Shipment toModel(Shipment resource) {

    // Add HATEOAS links
    resource.add(linkTo(methodOn(ShipmentController.class).getShipmentByOrderId(resource.getId())).withRel("byOrderId"));

    return resource;
  }

  /**
   * Coverts the collection of Product resources to HATEOAS resources.
   * @param resources
   * @return
   */
  public List<Shipment> toModelList(List<Shipment> resources) {
    return resources.stream().map(this::toModel).toList();
  }
}

