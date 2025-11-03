package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.mappers.AddressMapper;
import com.example.ecommercedemo.mappers.CardMapper;
import com.example.ecommercedemo.mappers.UserMapper;
import com.example.ecommercedemo.model.Address;
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
  private final AddressMapper addressMapper;
  private final CardMapper cardMapper;

  public UserServiceImpl(UserRepository repository, UserMapper userMapper, AddressMapper addressMapper, CardMapper cardMapper) {
    this.repository = repository;
    this.userMapper = userMapper;
    this.addressMapper = addressMapper;
    this.cardMapper = cardMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public void deleteCustomerById(UUID id) {
    repository.deleteById(id);
  }

  // TO DO: MOVE TO ADDRESS SERVICE
  @Override
  @Transactional(readOnly = true)
  public Optional<List<Address>> getAddressesByCustomerId(UUID id) {
    return repository.findById(id)                 // Returns Optional<UserEntity>
        .map(UserEntity::getAddresses)         // Returns Optional<List<AddressEntity>>
        .map(addressMapper::entityToModelList);  // Returns Optional<List<Address>>
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getAllCustomers() {
    List<UserEntity> entities = repository.findAll();
    return userMapper.entityToModelList(entities);
  }

  // TO DO MOVE TO CARD SERVICE
  @Override
  @Transactional(readOnly = true)
  public Optional<Card> getCardByCustomerId(UUID id) {
    return repository.findById(id)
        .map(UserEntity::getCards)    // List<CardEntity>
        .filter(cards -> !cards.isEmpty())
        .map(cards -> cards.get(0)) // CardEntity
        .map(cardMapper::entityToModel);             // Card model
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> getCustomerById(UUID id) {
    return repository.findById(id)
        .map(userMapper::entityToModel);
  }
}

