package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="cart")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = {"customer", "items"})
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class CartEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID")
  private CustomerEntity customer;

  @ManyToMany
  @JoinTable(
      name = "CART_ITEM",
      joinColumns = @JoinColumn(name = "CART_ID"),
      inverseJoinColumns = @JoinColumn(name = "ITEM_ID"))
  private List<ItemEntity> items = new ArrayList<>();

}
