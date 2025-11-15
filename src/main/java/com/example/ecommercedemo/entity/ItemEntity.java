package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = {"product", "cart", "orders"})
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class ItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")
  private ProductEntity product;

  @Column(name = "UNIT_PRICE")
  @ToString.Include
  private BigDecimal price;

  @Column(name = "QUANTITY")
  @ToString.Include
  private int quantity;

  @ManyToMany(mappedBy = "items", fetch = FetchType.LAZY)
  private List<CartEntity> cart;

  @ManyToMany(mappedBy = "items", fetch = FetchType.LAZY)
  private List<OrderEntity> orders;

}
