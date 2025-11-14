package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.exceptions.ErrorCode;
import com.example.ecommercedemo.exceptions.GenericAlreadyExistsException;
import com.example.ecommercedemo.mappers.UserMapper;
import com.example.ecommercedemo.model.Customer;
import com.example.ecommercedemo.model.CustomerReq;
import com.example.ecommercedemo.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class CustomerServiceImpl implements CustomerService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public CustomerServiceImpl(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  @Transactional
  public Customer createUser(@Valid CustomerReq customerReq) {
    if (customerReq == null) {
      throw new IllegalArgumentException("UserReq cannot be null");
    }
    if (customerReq.getUsername() == null || customerReq.getUsername().isEmpty()) {
      throw new IllegalArgumentException("UserName cannot be null");
    }
    if (userRepository.existsByUsername(customerReq.getUsername())) {
      throw new GenericAlreadyExistsException(ErrorCode.GENERIC_ALREADY_EXISTS);
    }

    UserEntity userEntity = new UserEntity()
        .setUsername(customerReq.getUsername())
        .setFirstName(customerReq.getFirstName())
        .setLastName(customerReq.getLastName())
        .setEmail(customerReq.getEmail())
        .setPhone(customerReq.getPhone())
        .setUserStatus(customerReq.getUserStatus());

    UserEntity savedUser = userRepository.save(userEntity);
    return userMapper.entityToModel(savedUser);
  }

  @Override
  @Transactional
  public Customer updateUser(@NotNull(message = "Customer UUID cannot be null.") UUID customerId, @Valid CustomerReq customerReq) {
    if (customerReq == null) {
      throw new IllegalArgumentException("UserReq cannot be null");
    }
    if (customerReq.getUsername() == null || customerReq.getUsername().isEmpty()) {
      throw new IllegalArgumentException("UserName cannot be null");
    }

    // 1.  Check if customer exists and retrieve existing customer
    UserEntity existingUserEntity = userRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));

    // 2. Check for Username Conflict
    String newUsername = customerReq.getUsername();
    // Check 2a: Only perform a uniqueness check if the username has actually changed.
    if (!existingUserEntity.getUsername().equals(newUsername)) {
      // Check 2b: If the new username exists in the repository (i.e., belongs to someone else)
      if (userRepository.existsByUsername(newUsername)) {
        throw new GenericAlreadyExistsException(ErrorCode.GENERIC_ALREADY_EXISTS);
      }
    }

    existingUserEntity
        .setUsername(customerReq.getUsername())
        .setFirstName(customerReq.getFirstName())
        .setLastName(customerReq.getLastName())
        .setEmail(customerReq.getEmail())
        .setPhone(customerReq.getPhone())
        .setUserStatus(customerReq.getUserStatus());

    UserEntity updatedUser = userRepository.save(existingUserEntity);
    return userMapper.entityToModel(updatedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Customer> getAllCustomers() {
    List<UserEntity> entities = userRepository.findAll();
    return userMapper.entityToModelList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Customer> getCustomerById(UUID id) {
    return userRepository.findById(id)
        .map(userMapper::entityToModel);
  }

  @Override
  @Transactional
  public void deleteCustomerById(UUID id) {
    userRepository.deleteById(id);
  }
}

