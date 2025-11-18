package com.example.ecommercedemo.order;

import com.example.ecommercedemo.address.AddressEntity;
import com.example.ecommercedemo.card.CardEntity;
import com.example.ecommercedemo.customer.CustomerEntity;
import com.example.ecommercedemo.payment.PaymentEntity;
import com.example.ecommercedemo.shipment.ShipmentEntity;
import com.example.ecommercedemo.item.ItemEntity;
import com.example.ecommercedemo.payment.AuthorizationEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.ecommercedemo.model.Order.StatusEnum;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = {
    "customerEntity",
    "addressEntity",
    "paymentEntity",
    "shipment",
    "cardEntity",
    "items",
    "authorizationEntity"
})
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class OrderEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @Column(name = "TOTAL")
  @ToString.Include
  private BigDecimal total;

  @Column(name = "ORDER_DATE")
  @ToString.Include
  private Timestamp orderDate;

  @Column(name = "STATUS")
  @Enumerated(EnumType.STRING)
  @ToString.Include
  private StatusEnum status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="CUSTOMER_ID", nullable=false)
  private CustomerEntity customerEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ADDRESS_ID", referencedColumnName = "ID")
  @JsonBackReference
  private AddressEntity addressEntity;

  // A Payment only belongs to one Order, so deleting the Order should delete the Payment record.
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL )
  @JoinColumn(name = "PAYMENT_ID", referencedColumnName = "ID")
  private PaymentEntity paymentEntity;

  // A Shipment only belongs to one Order, so deleting the Order should delete the Shipment record.
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "SHIPMENT_ID", referencedColumnName = "ID")
  private ShipmentEntity shipment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARD_ID", referencedColumnName = "ID")
  private CardEntity cardEntity;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "ORDER_ITEM",
      joinColumns = @JoinColumn(name = "ORDER_ID"),
      inverseJoinColumns = @JoinColumn(name = "ITEM_ID")
  )
  private List<ItemEntity> items = new ArrayList<>();

  // orphanRemoval = true because if an authorization is removed from its parent order
  // then the authorization should be deleted
  @OneToOne(mappedBy = "orderEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private AuthorizationEntity authorizationEntity;

}
