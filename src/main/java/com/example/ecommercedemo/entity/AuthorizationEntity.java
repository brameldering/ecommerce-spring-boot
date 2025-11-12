package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "\"authorization\"")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = {"orderEntity"})
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class AuthorizationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @Column(name="AUTHORIZED")
  @ToString.Include
  private boolean authorized;

  @Column(name="TIME")
  @ToString.Include
  private Timestamp time;

  @Column(name = "MESSAGE")
  @ToString.Include
  private String message;

  @Column(name = "ERROR")
  @ToString.Include
  private String error;

  @OneToOne
  @JoinColumn(name = "ORDER_ID", referencedColumnName = "id")
  private OrderEntity orderEntity;

}
