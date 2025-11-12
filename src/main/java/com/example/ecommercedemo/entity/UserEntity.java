package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"user\"") // with \" because user is a reserved word in SQL
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = {"addresses", "cards", "cart", "orders"})
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @NotNull(message = "User name is required.")
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

  @Column(name = "USER_STATUS")
  @ToString.Include
  private String userStatus;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "USER_ADDRESS",
      joinColumns = @JoinColumn(name = "USER_ID"),
      inverseJoinColumns = @JoinColumn(name = "ADDRESS_ID")
  )
  private List<AddressEntity> addresses = new ArrayList<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
  private List<CardEntity> cards;

  // , orphanRemoval = true
  @OneToOne(mappedBy = "user")
  private CartEntity cart;

  @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
  private List<OrderEntity> orders;

}

