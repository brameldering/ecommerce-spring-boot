package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
}
