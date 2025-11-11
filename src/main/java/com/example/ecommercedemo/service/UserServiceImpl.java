package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.mappers.AddressMapper;
import com.example.ecommercedemo.mappers.CardMapper;
import com.example.ecommercedemo.mappers.UserMapper;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.model.User;
import com.example.ecommercedemo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class UserServiceImpl implements UserService {

  private final UserRepository repository;
  private final UserMapper userMapper;
  private final CardMapper cardMapper;

  public UserServiceImpl(UserRepository repository, UserMapper userMapper, CardMapper cardMapper) {
    this.repository = repository;
    this.userMapper = userMapper;
    this.cardMapper = cardMapper;
  }
  @Override
  @Transactional(readOnly = true)
  public List<User> getAllCustomers() {
    List<UserEntity> entities = repository.findAll();
    return userMapper.entityToModelList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> getCustomerById(UUID id) {
    return repository.findById(id)
        .map(userMapper::entityToModel);
  }

  @Override
  @Transactional
  public void deleteCustomerById(UUID id) {
    repository.deleteById(id);
  }
}

