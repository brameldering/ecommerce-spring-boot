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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final static Logger LOGGER = LoggerFactory.getLogger(AddressServiceImpl.class);

  public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository, AddressMapper mapper) {
    this.addressRepository = addressRepository;
    this.userRepository = userRepository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public Address createAddress(UUID customerId, AddressReq addressReq) {
    LOGGER.info("Creating address with customer id {}", customerId);

    // --- VALIDATION ---
    if (addressReq == null) {
      throw new IllegalArgumentException("Address cannot be null.");
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
    // --- END VALIDATION ---

    // 1. Find the User (this replaces the old validation)
    UserEntity user = userRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));

    // 2. Map the DTO (which no longer has userId) to an entity
    AddressEntity newAddress = mapper.addressReqToEntity(addressReq);

    // 3. Set the user for the new address
    newAddress.setUser(user);

    // 4. Save the new address entity
    AddressEntity savedAddress = addressRepository.save(newAddress);

    // 5. Map the saved address entity back to the model and return it
    return mapper.entityToModel(savedAddress);
//    return mapper.entityToModel(addressRepository.save(mapper.addressReqToEntity(addressReq)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Address> getAllAddresses() {
    return mapper.entityToModelList(addressRepository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Address> getAddressById(UUID AddressId) {
    return addressRepository.findById(AddressId).map(mapper::entityToModel);
   }

  @Override
  @Transactional(readOnly = true)
  public List<Address> getAddressesByCustomerId(UUID customerId) {
//    return userRepository.findById(id) // Returns Optional<UserEntity>
//        .map(UserEntity::getAddresses) // Returns Optional<List<AddressEntity>>
//        .map(mapper::entityToModelList); // Returns Optional<List<Address>>

    // 1. Check if the user exists. If not, throw CustomerNotFoundException.
    UserEntity user = userRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));

    // 2. Get the list of addresses (which is guaranteed non-null, potentially empty).
    List<AddressEntity> addressEntities = user.getAddresses();

    // 3. Map the non-null list and return it directly.
    return mapper.entityToModelList(addressEntities);
  }

  @Override
  @Transactional
  public void deleteAddressById(UUID AddressId) {
    addressRepository.deleteById(AddressId);
  }
}