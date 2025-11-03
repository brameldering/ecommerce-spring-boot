package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.ProductController;
import com.example.ecommercedemo.model.Product;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<Product, Product> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport}
   * using the given controller class and resource type.
   */
  public ProductRepresentationModelAssembler() {
    super(ProductController.class, Product.class);
  }

  /**
   * Converts the resource to HATEOAS resource
   *
   * @param resource
   */
  @Override
  public Product toModel(Product resource) {

    // Add HATEOAS links
    resource.add(linkTo(methodOn(ProductController.class).getProduct(resource.getId())).withSelfRel());

    resource.add(linkTo(methodOn(ProductController.class).queryProducts(null, null, 1, 10)).withRel("products"));

    return resource;
  }

  /**
   * Converts the collection of Product entities to list of resources.
   *
   * @param resources
   */
  public List<Product> toListModel(List<Product> resources) {
    return resources.stream().map(this::toModel).toList();
  }
}

