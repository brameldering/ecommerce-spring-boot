package com.example.ecommercedemo.repository;

import java.util.List;
import java.util.UUID;

import com.example.ecommercedemo.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {
  @Query(
      value = """
      select item.* from ecomm.cart
      join ecomm.cart_item on cart.id = cart_item.cart_id
      join ecomm.item on item.id = cart_item.item_id
      join ecomm.customer on customer.id = cart.customer_id
      where customer.id = :customerId
    """,
      nativeQuery = true)
  List<ItemEntity> findByCustomerId(UUID customerId);

  @Modifying
  @Query(
      value = "delete from ecomm.cart_item where item_id in (:ids) and cart_id = :cartId",
      nativeQuery = true)
  void deleteCartItemJoinById(List<UUID> ids, UUID cartId);
}

