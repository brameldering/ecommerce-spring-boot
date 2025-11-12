package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "card")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = {"user", "orders"})
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class CardEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @Column(name = "NUMBER")
  @ToString.Include
  private String number;

  @Column(name = "EXPIRES")
  @ToString.Include
  private String expires;

  @Column(name = "CVV")
  @ToString.Include
  private String cvv;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
  private UserEntity user;

  @OneToMany(mappedBy = "cardEntity", fetch = FetchType.LAZY)
  private List<OrderEntity> orders;

}
