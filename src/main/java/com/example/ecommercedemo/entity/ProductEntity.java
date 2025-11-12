package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = {"tags", "items"})
@NoArgsConstructor  // Generates the empty constructor for JPA
@AllArgsConstructor // Generates the constructor with all 6 fields
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class ProductEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @NotNull(message = "Product name is required.")
  @Basic(optional = false)
  @Column(name = "NAME")
  @ToString.Include
  private String name;

  @Column(name = "DESCRIPTION")
  @ToString.Include
  private String description;

  @NotNull(message = "Product price is required.")
  @Column(name = "PRICE")
  @ToString.Include
  private BigDecimal price;

  @Column(name = "COUNT")
  @ToString.Include
  private int count;

  @Column(name = "IMAGE_URL")
  @ToString.Include
  private String imageUrl;

  @ManyToMany
  @JoinTable(
      name = "PRODUCT_TAG",
      joinColumns = @JoinColumn(name = "PRODUCT_ID"),
      inverseJoinColumns = @JoinColumn(name = "TAG_ID")
  )
  private List<TagEntity> tags = new ArrayList<>();

  @OneToMany(mappedBy = "product")
  private List<ItemEntity> items;
}