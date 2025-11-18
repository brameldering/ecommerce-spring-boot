package com.example.ecommercedemo.order;

import com.example.ecommercedemo.model.Order;
import com.example.ecommercedemo.model.OrderReq;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;

  private final OrderMapper orderMapper;

  public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
    this.orderRepository = orderRepository;
    this.orderMapper = orderMapper;
  }

  @Override
  @Transactional
  public Order addOrder(UUID customerId, OrderReq orderReq) {

    if (orderReq == null) {
      throw new IllegalArgumentException("Order cannot be null.");
    }
    if (customerId == null) {
      throw new IllegalArgumentException("Customer ID cannot be null.");
    }
    if (orderReq.getAddressId() == null) {
      throw new IllegalArgumentException("Address ID cannot be null.");
    }
    if (orderReq.getCardId() == null) {
      throw new IllegalArgumentException("Card ID cannot be null.");
    }

    // 1. Add HATEOAS links
    OrderEntity createdOrderEntity = orderRepository.insert(customerId, orderReq);
    return orderMapper.entityToModel(createdOrderEntity);
    // Ideally, here it will trigger the rest of the process
    // 2. Initiate the payment
    // 3. Once the payment is authorized, change the status to paid
    // 4. Initiate the Shipment and changed the status to Shipment Initiated or Shipped
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrders() {
    return orderMapper.entityToModelList(orderRepository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Order> getOrderById(UUID orderId) {
    return orderRepository.findById(orderId).map(orderMapper::entityToModel);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getOrdersByCustomerId(UUID customerId) {
    // 1. Get the list of OrderEntity objects (returns List<OrderEntity>, potentially empty but never null)
    List<OrderEntity> orderEntities = orderRepository.findByCustomerId(customerId);

    // 2. Map the List of Entities to a List of Models using the mapper
    return orderMapper.entityToModelList(orderEntities);
  }
}

