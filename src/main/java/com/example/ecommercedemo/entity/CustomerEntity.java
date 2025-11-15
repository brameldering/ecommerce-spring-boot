package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = {"addresses", "cards", "cart", "orders"})
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class CustomerEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @NotNull(message = "Customer username is required.")
  @Basic(optional = false)
  @Column(name = "USERNAME", unique = true, nullable = false, length = 16)
  @ToString.Include
  private String username;

  @Column(name = "FIRST_NAME")
  @ToString.Include
  private String firstName;

  @Column(name = "LAST_NAME")
  @ToString.Include
  private String lastName;

  @Column(name = "EMAIL")
  @ToString.Include
  private String email;

  @Column(name = "PHONE")
  @ToString.Include
  private String phone;

  @Column(name = "STATUS")
  @ToString.Include
  private String status;

  @OneToMany(
      mappedBy = "customer", // "customer" is the field name in AddressEntity
      cascade = CascadeType.ALL, // Save/update/delete addresses with the customer
      orphanRemoval = true // Delete addresses that are no longer linked to this customer
  )
  private List<AddressEntity> addresses = new ArrayList<>();

  @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, orphanRemoval = true)
  private List<CardEntity> cards;

  // , orphanRemoval = true
  @OneToOne(mappedBy = "customer")
  private CartEntity cart;

  @OneToMany(mappedBy = "customerEntity", fetch = FetchType.LAZY)
  private List<OrderEntity> orders;

}

