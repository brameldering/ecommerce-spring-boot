package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "\"authorization\"")
@Data // Generates getters, setters, toString, equals, and hashCode
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class AuthorizationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  private UUID id;

  @Column(name="AUTHORIZED")
  private boolean authorized;

  @Column(name="TIME")
  private Timestamp time;

  @Column(name = "MESSAGE")
  private String message;

  @Column(name = "ERROR")
  private String error;

  @OneToOne
  @JoinColumn(name = "ORDER_ID", referencedColumnName = "id")
  @ToString.Exclude
  private OrderEntity orderEntity;

}
