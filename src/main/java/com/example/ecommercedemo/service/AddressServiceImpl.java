package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.AddressEntity;
import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.exceptions.ErrorCode;
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
  public Address createAddress(AddressReq addressReq) {
    // --- VALIDATION ---
    if (addressReq == null) {
      throw new IllegalArgumentException("Address cannot be null.");
    }
    if (addressReq.getUserId() == null) {
      throw new IllegalArgumentException("UserId cannot be null.");
    }
    if (addressReq.getStreet() == null || addressReq.getStreet().isBlank()) {
      throw new IllegalArgumentException("Street cannot be empty.");
    }
    if (addressReq.getCity() == null || addressReq.getCity().isBlank()) {
      throw new IllegalArgumentException("City cannot be empty.");
    }
    if (addressReq.getZipcode() == null || addressReq.getZipcode().isBlank()) {
      throw new IllegalArgumentException("Zipcode cannot be empty.");
    }
    // Check if user exists
    if (!userRepository.existsById(addressReq.getUserId())) {
      throw new CustomerNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND);
    }
    // --- END VALIDATION ---

    return mapper.entityToModel(addressRepository.save(mapper.addressReqToEntity(addressReq)));
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
  public List<Address> getAddressesByCustomerId(UUID id) {
//    return userRepository.findById(id) // Returns Optional<UserEntity>
//        .map(UserEntity::getAddresses) // Returns Optional<List<AddressEntity>>
//        .map(mapper::entityToModelList); // Returns Optional<List<Address>>

    // 1. Check if the user exists. If not, throw CustomerNotFoundException.
    UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new CustomerNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));

    // 2. Get the list of addresses (which is guaranteed non-null, potentially empty).
    List<AddressEntity> addressEntities = user.getAddresses();

    // 3. Map the non-null list and return it directly.
    return mapper.entityToModelList(addressEntities);
  }

  @Override
  @Transactional
  public void deleteAddressById(UUID uuid) {
    addressRepository.deleteById(uuid);
  }
}