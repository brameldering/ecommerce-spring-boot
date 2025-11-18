package com.example.ecommercedemo.card;

import com.example.ecommercedemo.customer.CustomerEntity;
import com.example.ecommercedemo.order.OrderEntity;
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
@EqualsAndHashCode(exclude = {"customer", "orders"})
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class CardEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @Column(name = "NUMBER", unique = true, nullable = false)
  @ToString.Include
  private String number;

  @Column(name = "EXPIRES")
  @ToString.Include
  private String expires;

  @Column(name = "CVV")
  @ToString.Include
  private String cvv;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID")
  private CustomerEntity customer;

  @OneToMany(mappedBy = "cardEntity", fetch = FetchType.LAZY)
  private List<OrderEntity> orders;

}
