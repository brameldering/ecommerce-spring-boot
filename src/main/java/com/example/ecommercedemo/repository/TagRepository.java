package com.example.ecommercedemo.repository;

import com.example.ecommercedemo.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<TagEntity, UUID> {
}
