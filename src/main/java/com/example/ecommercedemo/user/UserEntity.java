package com.example.ecommercedemo.user;

import com.example.ecommercedemo.auth.RoleEnum;
import com.example.ecommercedemo.customer.CustomerEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.UUID;

@Entity
@Table(name = "user")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = {"customer"})
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @NotNull(message = "Username is required.")
  @Basic(optional = false)
  @Column(name = "USERNAME", unique = true, nullable = false, length = 16)
  @ToString.Include
  private String username;

  @Column(name = "PASSWORD")
  private String password;

  @Column(name = "ROLE")
  @Enumerated(EnumType.STRING)
  private RoleEnum role = RoleEnum.USER;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID")
  private CustomerEntity customer;
}

