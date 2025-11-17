package com.example.ecommercedemo.mappers;

import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class OrderMapper {

  private final CustomerMapper customerMapper;
  private final AddressMapper addressMapper;
  private final CardMapper cardMapper;
  private final PaymentMapper paymentMapper;
  private final ShipmentMapper shipmentMapper;
  private final ItemMapper itemMapper;

  private final static Logger LOGGER = LoggerFactory.getLogger(OrderMapper.class);

  public OrderMapper(CustomerMapper customerMapper, AddressMapper addressMapper, CardMapper cardMapper, PaymentMapper paymentMapper, ShipmentMapper shipmentMapper, ItemMapper itemMapper) {
    this.customerMapper = customerMapper;
    this.addressMapper = addressMapper;
    this.cardMapper = cardMapper;
    this.paymentMapper = paymentMapper;
    this.shipmentMapper = shipmentMapper;
    this.itemMapper = itemMapper;
  }

  // Transform from entity to model
  public Order entityToModel(OrderEntity entity) {
    if (entity == null) {
      return null;
    }

    Order resource = new Order();

    // Retrieve customer id and cart id
//    UUID customerId = Objects.nonNull(entity.getCustomerEntity()) ? entity.getCustomerEntity().getId() : null;

    // Copy properties and set ID
//    BeanUtils.copyProperties(entity, resource);
//    LOGGER.info("---> Order resource after copyProperties -> {}", resource);

    // Map all properties
    resource.id(entity.getId())
            .customer(customerMapper.entityToModel(entity.getCustomerEntity()))
            .address(addressMapper.entityToModel(entity.getAddressEntity()))
            .card(cardMapper.entityToModel(entity.getCardEntity()))
            .date(entity.getOrderDate().toInstant().atOffset(ZoneOffset.UTC))
            .payment(paymentMapper.entityToModel(entity.getPaymentEntity()))
            .shipment(shipmentMapper.entityToModel(entity.getShipment()))
            .status(entity.getStatus())
            .items(itemMapper.entityToModelList(entity.getItems()));

    if (entity.getTotal() != null) {
      resource.total(entity.getTotal().toString());
    }

    LOGGER.info("---> Order resource after mapping assignments -> {}", resource);

    return resource;
  }

  // Transform from entity list to model list
  public List<Order> entityToModelList(List<OrderEntity> entities) {
    return entities.stream().map(this::entityToModel).toList();
  }
}
