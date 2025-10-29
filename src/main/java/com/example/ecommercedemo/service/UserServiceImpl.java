package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.hateoas.UserRepresentationModelAssembler;
import com.example.ecommercedemo.hateoas.AddressRepresentationModelAssembler;
import com.example.ecommercedemo.hateoas.CardRepresentationModelAssembler;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.model.Card;
import com.example.ecommercedemo.model.User;
import com.example.ecommercedemo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository repository;
  private final UserRepresentationModelAssembler userAssembler;
  private final AddressRepresentationModelAssembler addressAssembler;
  private final CardRepresentationModelAssembler cardAssembler;

  public UserServiceImpl(UserRepository repository, UserRepresentationModelAssembler userAssembler, AddressRepresentationModelAssembler addressAssembler, CardRepresentationModelAssembler cardAssembler) {
    this.repository = repository;
    this.userAssembler = userAssembler;
    this.addressAssembler = addressAssembler;
    this.cardAssembler = cardAssembler;
  }

  @Override
  @Transactional(readOnly = true)
  public void deleteCustomerById(String id) {
    repository.deleteById(UUID.fromString(id));
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<List<Address>> getAddressesByCustomerId(String id) {
    return repository.findById(UUID.fromString(id))
        .map(UserEntity::getAddresses)
        .map(addressAssembler::toListModel);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getAllCustomers() {
    List<UserEntity> entities = repository.findAll();
    return userAssembler.toListModel(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Card> getCardByCustomerId(String id) {
    return repository.findById(UUID.fromString(id))
        .map(UserEntity::getCards)    // List<CardEntity>
        .filter(cards -> !cards.isEmpty())
        .map(cards -> cards.get(0)) // CardEntity
        .map(cardAssembler::toModel);             // Card model
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> getCustomerById(String id) {
    return repository.findById(UUID.fromString(id))
        .map(userAssembler::toModel);
  }
}

