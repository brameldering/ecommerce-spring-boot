package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Entity
@Table(name = "payment")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = {"orderEntity"})
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class PaymentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @Column(name="AUTHORIZED")
  @ToString.Include
  private boolean authorized;

  @Column(name = "MESSAGE")
  @ToString.Include
  private String message;

  @OneToOne(mappedBy = "paymentEntity")
  private OrderEntity orderEntity;

}
