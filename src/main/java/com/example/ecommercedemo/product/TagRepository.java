package com.example.ecommercedemo.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<TagEntity, UUID> {
}
