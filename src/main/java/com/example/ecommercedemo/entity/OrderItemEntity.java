package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class OrderItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "order_id")
  private UUID orderId;

  @Column(name = "item_id")
  private UUID itemId;

}
