package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.mappers.AddressMapper;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.model.AddressReq;
import com.example.ecommercedemo.repository.AddressRepository;
import com.example.ecommercedemo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class AddressServiceImpl implements AddressService {

  private final AddressRepository addressRepository;
  private final UserRepository userRepository;
  private final AddressMapper mapper;

  public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository, AddressMapper mapper) {
    this.addressRepository = addressRepository;
    this.userRepository = userRepository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public Optional<Address> createAddress(AddressReq addressReq) {
    return Optional.of(mapper.entityToModel(addressRepository.save(mapper.addressReqToEntity(addressReq))));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Address> getAllAddresses() {
    return mapper.entityToModelList(addressRepository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Address> getAddressById(UUID uuid) {
    return addressRepository.findById(uuid).map(mapper::entityToModel);
   }

  @Override
  @Transactional(readOnly = true)
  public Optional<List<Address>> getAddressesByCustomerId(UUID id) {
    return userRepository.findById(id) // Returns Optional<UserEntity>
        .map(UserEntity::getAddresses) // Returns Optional<List<AddressEntity>>
        .map(mapper::entityToModelList); // Returns Optional<List<Address>>
  }

  @Override
  @Transactional
  public void deleteAddressById(UUID uuid) {
    addressRepository.deleteById(uuid);
  }
}