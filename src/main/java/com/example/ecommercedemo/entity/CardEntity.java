package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "card")
@Data // Generates getters, setters, toString, equals, and hashCode
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class CardEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "NUMBER")
  private String number;

  @Column(name = "EXPIRES")
  private String expires;

  @Column(name = "CVV")
  private String cvv;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
  private UserEntity user;

  @OneToMany(mappedBy = "cardEntity", fetch = FetchType.LAZY, orphanRemoval = true)
  @ToString.Exclude
  private List<OrderEntity> orders;

}
