package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="cart")
@Data // Generates getters, setters, toString, equals, and hashCode
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class CartEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  private UUID id;

  @OneToOne
  @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
  @ToString.Exclude
  private UserEntity user;

  @ManyToMany
  @JoinTable(
      name = "CART_ITEM",
      joinColumns = @JoinColumn(name = "CART_ID"),
      inverseJoinColumns = @JoinColumn(name = "ITEM_ID"))
  @ToString.Exclude
  private List<ItemEntity> items = new ArrayList<>();

}
