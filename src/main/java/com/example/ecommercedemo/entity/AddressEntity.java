package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "address")
@Data // Generates getters, setters, toString, equals, and hashCode
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class AddressEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "NUMBER")
  private String number;

  @Column(name = "RESIDENCY")
  private String residency;

  @Column(name = "STREET")
  private String street;

  @Column(name = "CITY")
  private String city;

  @Column(name = "STATE")
  private String state;

  @Column(name = "COUNTRY")
  private String country;

  @Column(name = "ZIPCODE")
  private String zipcode;

  @OneToMany(mappedBy = "addressEntity", fetch = FetchType.LAZY, orphanRemoval = true)
  private List<OrderEntity> orders;

}
