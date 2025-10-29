package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.UUID;

@Entity
@Table(name = "payment")
@Data // Generates getters, setters, toString, equals, and hashCode
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class PaymentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  private UUID id;

  @Column(name="AUTHORIZED")
  private boolean authorized;

  @Column(name = "MESSAGE")
  private String message;

  @OneToOne(mappedBy = "paymentEntity")
  @ToString.Exclude
  private OrderEntity orderEntity;

}
