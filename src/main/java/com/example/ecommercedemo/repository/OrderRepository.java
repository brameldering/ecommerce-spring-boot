package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID>, OrderRepositoryExt {
  @Query("select o from OrderEntity o join o.userEntity u where u.id = :customerId")
  List<OrderEntity> findByCustomerId(@Param("customerId") UUID customerId);
}
