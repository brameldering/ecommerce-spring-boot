package com.example.ecommercedemo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "address")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = {"user", "orders"})
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class AddressEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @Column(name = "NUMBER")
  @ToString.Include
  private String number;

  @Column(name = "RESIDENCY")
  @ToString.Include
  private String residency;

  @NotNull(message = "Street is required.")
  @Basic(optional = false)
  @Column(name = "STREET")
  @ToString.Include
  private String street;

  @NotNull(message = "City is required.")
  @Basic(optional = false)
  @Column(name = "CITY")
  @ToString.Include
  private String city;

  @Column(name = "STATE")
  @ToString.Include
  private String state;

  @Column(name = "COUNTRY")
  @ToString.Include
  private String country;

  @NotNull(message = "Zipcode is required.")
  @Basic(optional = false)
  @Column(name = "ZIPCODE")
  @ToString.Include
  private String zipcode;

  // @ManyToMany(mappedBy = "addresses", fetch = FetchType.LAZY)
  // private List<UserEntity> users;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USER_ID", nullable = false) // creates a USER_ID foreign key
  private UserEntity user;

  @OneToMany(mappedBy = "addressEntity", fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<OrderEntity> orders;

}
