package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.ProductEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends CrudRepository<ProductEntity, UUID> {

  @Query("select distinct p from ProductEntity p left join fetch p.tags")
  List<ProductEntity> findAllWithTags();

  @Query("select p from ProductEntity p left join fetch p.tags where p.id = :id")
  Optional<ProductEntity> findByIdWithTags(@Param("id") UUID id);
}
