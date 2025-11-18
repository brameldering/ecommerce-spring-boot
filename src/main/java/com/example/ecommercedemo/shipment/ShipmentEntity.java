package com.example.ecommercedemo.shipment;

import com.example.ecommercedemo.order.OrderEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "shipment")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = {"orderEntity"})
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class ShipmentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @Column(name = "EST_DELIVERY_DATE")
  @ToString.Include
  private Timestamp estDeliveryDate;

  @Column(name = "CARRIER")
  @ToString.Include
  private String carrier;

  @OneToOne(mappedBy = "shipment")
  private OrderEntity orderEntity;
}
