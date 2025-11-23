package com.example.ecommercedemo.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<UserEntity, UUID> {

  Optional<UserEntity> findByUsername(String username);

  @Query(
      value =
          "select count(u.*) from ecomm.\"user\" u where u.username = :username",
      nativeQuery = true)
  Integer findByUsernameCount(String username);
}

