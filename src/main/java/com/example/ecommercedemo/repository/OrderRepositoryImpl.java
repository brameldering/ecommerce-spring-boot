package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.CartEntity;
import com.example.ecommercedemo.entity.ItemEntity;
import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.entity.OrderItemEntity;
import com.example.ecommercedemo.exceptions.ItemNotFoundException;
import com.example.ecommercedemo.exceptions.ResourceNotFoundException;
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

@Repository
@Transactional
public class OrderRepositoryImpl implements OrderRepositoryExt{

  @PersistenceContext
  private final EntityManager em;

  private final ItemRepository itemRepo;
  private final CartRepository cRepo;
  private final OrderItemRepository oiRepo;

  public OrderRepositoryImpl(
      EntityManager em, ItemRepository itemRepo, CartRepository cRepo, OrderItemRepository oiRepo) {
    this.em = em;
    this.itemRepo = itemRepo;
    this.cRepo = cRepo;
    this.oiRepo = oiRepo;
  }

  @Override
  public Optional<OrderEntity> insert(OrderReq orderReq) {
    // Items are in db (cart, cart_item and item) and saved to db as an order
    List<ItemEntity> items = itemRepo.findByCustomerId(orderReq.getCustomerId());
    if (items.isEmpty()) {
      throw new ItemNotFoundException(
          String.format("There are no items found in customer's (ID: %s) cart.", orderReq.getCustomerId()));
    }
    BigDecimal total = BigDecimal.ZERO;
    for (ItemEntity i : items) {
      total = (BigDecimal.valueOf(i.getQuantity()).multiply(i.getPrice())).add(total);
    }
    Timestamp orderDate = Timestamp.from(Instant.now());

    em.createNativeQuery(
            """
        INSERT INTO ecomm.orders (address_id, card_id, customer_id, order_date, total, status)
         VALUES(?, ?, ?, ?, ?, ?)
        """)
        .setParameter(1, orderReq.getAddressId())
        .setParameter(2, orderReq.getCardId())
        .setParameter(3, orderReq.getCustomerId())
        .setParameter(4, orderDate)
        .setParameter(5, total)
        .setParameter(6, Order.StatusEnum.CREATED.getValue())
        .executeUpdate();

    Optional<CartEntity> oCart = cRepo.findByCustomerId(orderReq.getCustomerId());
    CartEntity cart =
        oCart.orElseThrow(
            () ->
                new ResourceNotFoundException(
                    String.format(
                        "Cart not found for given customer (ID: %s)", orderReq.getCustomerId())));

    itemRepo.deleteCartItemJoinById(
        cart.getItems().stream().map(ItemEntity::getId).toList(), cart.getId());

    OrderEntity entity =
        (OrderEntity)
            em.createNativeQuery(
                    """
        SELECT o.* FROM ecomm.orders o WHERE o.customer_id = ? AND o.order_date >= ?
        """,
                    OrderEntity.class)
                .setParameter(1, orderReq.getCustomerId())
                .setParameter(
                    2,
                    OffsetDateTime.ofInstant(orderDate.toInstant(), ZoneId.of("Z"))
                        .truncatedTo(ChronoUnit.MICROS))
                .getSingleResult();

    oiRepo.saveAll(
        cart.getItems().stream()
            .map(i -> new OrderItemEntity().setOrderId(entity.getId()).setItemId(i.getId())).toList());

    return Optional.of(entity);
  }
}
