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
      select i.* from ecomm.cart c
      join ecomm.cart_item ci on c.id = ci.cart_id
      join ecomm.item i on i.id = ci.item_id
      join ecomm."user" u on u.id = c.user_id
      where u.id = :customerId
    """,
      nativeQuery = true)
  List<ItemEntity> findByCustomerId(String customerId);

  @Modifying
  @Query(
      value = "delete from ecomm.cart_item where item_id in (:ids) and cart_id = :cartId",
      nativeQuery = true)
  void deleteCartItemJoinById(List<UUID> ids, UUID cartId);
}

