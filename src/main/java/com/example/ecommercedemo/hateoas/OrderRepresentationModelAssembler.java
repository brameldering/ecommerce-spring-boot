package com.example.ecommercedemo.hateoas;

import com.example.ecommercedemo.controllers.OrderController;
import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<OrderEntity, Order> {

  private final UserRepresentationModelAssembler uAssembler;
  private final AddressRepresentationModelAssembler aAssembler;
  private final CardRepresentationModelAssembler cAssembler;
//  private final ShipmentRepresentationModelAssembler sAssembler;
  private final ItemService itemService;

  private final Logger log = LoggerFactory.getLogger(OrderRepresentationModelAssembler.class);

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and
   * resource type.
   */
  public OrderRepresentationModelAssembler(UserRepresentationModelAssembler uAssembler,AddressRepresentationModelAssembler aAssembler, CardRepresentationModelAssembler cAssembler, ItemService itemService) {
    super(OrderController.class, Order.class);
    this.uAssembler = uAssembler;
    this.aAssembler = aAssembler;
    this.cAssembler = cAssembler;
//    this.sAssembler = sAssembler;
    this.itemService = itemService;
  }

  /**
   * Coverts the Order entity to resource
   *
   * @param entity
   */
  @Override
  public Order toModel(OrderEntity entity) {
    log.info("\nentity = " + entity +"\n");

    //    Order resource = createModelWithId(entity.getId(), entity);
    // 1. Manually instantiate the model instead of using createModelWithId
    Order resource = new Order();

    // 2. Copy properties and set ID
    BeanUtils.copyProperties(entity, resource);
    resource.id(entity.getId().toString())
        .customer(uAssembler.toModel(entity.getUserEntity()))
        .address(aAssembler.toModel(entity.getAddressEntity()))
        .card(cAssembler.toModel(entity.getCardEntity()))
        .items(itemService.toModelList(entity.getItems()))
        .date(entity.getOrderDate().toInstant().atOffset(ZoneOffset.UTC));

    log.info("\nresource = " + resource +"\n");

    // 3. Add HATEAOS links
    resource.add(linkTo(methodOn(OrderController.class).getByOrderId(entity.getId().toString())).withSelfRel());

    return resource;
  }

  /**
   * Coverts the collection of Product entities to list of resources.
   *
   * @param entities
   */
  public List<Order> toListModel(List<OrderEntity> entities) {
    return entities.stream().map(this::toModel).collect(toList());
  }

}
