package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.CartEntity;
import com.example.ecommercedemo.entity.ItemEntity;
import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.entity.OrderItemEntity;
import com.example.ecommercedemo.exceptions.ItemNotFoundException;
import com.example.ecommercedemo.exceptions.CartNotFoundException;
import com.example.ecommercedemo.model.OrderReq;
import com.example.ecommercedemo.model.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class OrderRepositoryImpl implements OrderRepositoryExt{

  @PersistenceContext
  private final EntityManager entityManager;

  private final ItemRepository itemRepository;
  private final CartRepository cartRepository;
  private final OrderItemRepository orderItemRepository;

  public OrderRepositoryImpl(
      EntityManager entityManager, ItemRepository itemRepository, CartRepository cartRepository, OrderItemRepository orderItemRepository) {
    this.entityManager = entityManager;
    this.itemRepository = itemRepository;
    this.cartRepository = cartRepository;
    this.orderItemRepository = orderItemRepository;
  }

  @Override
  public OrderEntity insert(UUID customerId, OrderReq orderReq) {
    // Items are in db (cart, cart_item and item) and saved to db as an order
    List<ItemEntity> items = itemRepository.findByCustomerId(customerId);
    if (items.isEmpty()) {
      throw new ItemNotFoundException(
          String.format("There are no items found in customer's (ID: %s) cart.", customerId));
    }
    BigDecimal total = BigDecimal.ZERO;
    for (ItemEntity i : items) {
      total = (BigDecimal.valueOf(i.getQuantity()).multiply(i.getPrice())).add(total);
    }
    Timestamp orderDate = Timestamp.from(Instant.now());

    entityManager.createNativeQuery(
            """
        INSERT INTO ecomm.orders (address_id, card_id, customer_id, order_date, total, status)
         VALUES(?, ?, ?, ?, ?, ?)
        """)
        .setParameter(1, orderReq.getAddressId())
        .setParameter(2, orderReq.getCardId())
        .setParameter(3, customerId)
        .setParameter(4, orderDate)
        .setParameter(5, total)
        .setParameter(6, Order.StatusEnum.CREATED.getValue())
        .executeUpdate();

    Optional<CartEntity> oCart = cartRepository.findByCustomerId(customerId);
    CartEntity cart =
        oCart.orElseThrow(
            () ->
                new CartNotFoundException(
                    String.format(
                        "Cart not found for given customer (ID: %s)", customerId)));

    itemRepository.deleteCartItemJoinById(
        cart.getItems().stream().map(ItemEntity::getId).toList(), cart.getId());

    OrderEntity entity =
        (OrderEntity)
            entityManager.createNativeQuery(
                    """
        SELECT o.* FROM ecomm.orders o WHERE o.customer_id = ? AND o.order_date >= ?
        """,
                    OrderEntity.class)
                .setParameter(1, customerId)
                .setParameter(
                    2,
                    OffsetDateTime.ofInstant(orderDate.toInstant(), ZoneId.of("Z"))
                        .truncatedTo(ChronoUnit.MICROS))
                .getSingleResult();

    orderItemRepository.saveAll(
        cart.getItems().stream()
            .map(i -> new OrderItemEntity().setOrderId(entity.getId()).setItemId(i.getId())).toList());

    return entity;
  }
}
