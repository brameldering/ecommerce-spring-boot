package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "item")
@Data // Generates getters, setters, toString, equals, and hashCode
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class ItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")
  @ToString.Exclude
  private ProductEntity product;

  @Column(name = "UNIT_PRICE")
  private BigDecimal price;

  @Column(name = "QUANTITY")
  private int quantity;

  @ManyToMany(mappedBy = "items", fetch = FetchType.LAZY)
  @ToString.Exclude
  private List<CartEntity> cart;

  @ManyToMany(mappedBy = "items", fetch = FetchType.LAZY)
  @ToString.Exclude
  private List<OrderEntity> orders;

}
