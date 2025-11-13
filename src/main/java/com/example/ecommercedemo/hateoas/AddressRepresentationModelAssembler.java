package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controller.AddressController;
import com.example.ecommercedemo.controller.CustomerController;
import com.example.ecommercedemo.model.Address;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
// The Entity and Model types are both 'Address' because the input
// from the service layer is already an Address model.
public class AddressRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<Address, Address> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport}
   * using the given controller class and resource type.
   */
  public AddressRepresentationModelAssembler() {
    super(AddressController.class, Address.class);
  }

  /**
   * Converts the resource to HATEOAS resource
   *
   * @param resource
   */
  @Override
  public Address toModel(Address resource) {

    // Add HATEOAS links
    resource.add(
        linkTo(methodOn(AddressController.class)
            .getAddressById(resource.getId()))
            .withSelfRel()
    );

    resource.add(
        linkTo(methodOn(CustomerController.class)
            .getCustomerById(resource.getUserId()))
        .withRel("customer")
    );

    return resource;
  }

  /**
   * Converts the collection of Address models to HATEOAS list of resources.
   *
   * @param resources
   */
  public List<Address> toModelList(List<Address> resources) {
    return resources.stream().map(this::toModel).toList();
  }
}
