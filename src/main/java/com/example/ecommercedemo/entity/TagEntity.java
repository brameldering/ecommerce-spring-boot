package com.example.ecommercedemo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Entity
@Table(name = "tag")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@Accessors(chain = true) // Enable fluent api, makes the setters return 'this'
public class TagEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", updatable = false, nullable = false)
  @ToString.Include
  private UUID id;

  @NotNull(message = "Tag name is required.")
  @Basic(optional = false)
  @Column(name = "NAME")
  @ToString.Include
  private String name;

}
