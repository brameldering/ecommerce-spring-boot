package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.ProductController;
import com.example.ecommercedemo.entity.ProductEntity;
import com.example.ecommercedemo.model.Product;
import com.example.ecommercedemo.model.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<ProductEntity, Product> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and
   * resource type.
   */
  public ProductRepresentationModelAssembler() {
    super(ProductController.class, Product.class);
  }

  /**
   * Coverts the Product entity to resource
   *
   * @param entity
   */
  @Override
  public Product toModel(ProductEntity entity) {

    //    Product resource = createModelWithId(entity.getId(), entity);
    // 1. Manually instantiate the model instead of using createModelWithId
    Product resource = new Product();

    // 2. Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId().toString());
    resource.setTag(
        entity.getTags().stream().map(t -> new Tag().id(t.getId().toString()).name(t.getName()))
            .collect(toList()));

    // 3. Add HATEAOS links
    resource.add(linkTo(methodOn(ProductController.class).getProduct(entity.getId().toString())).withSelfRel());

    resource.add(linkTo(methodOn(ProductController.class).queryProducts(null, null, 1, 10)).withRel("products"));

    return resource;
  }

  /**
   * Coverts the collection of Product entities to list of resources.
   *
   * @param entities
   */
  public List<Product> toListModel(List<ProductEntity> entities) {
    return entities.stream().map(this::toModel).collect(toList());
  }
}

