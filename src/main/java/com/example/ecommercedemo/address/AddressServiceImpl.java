package com.example.ecommercedemo.address;

import com.example.ecommercedemo.customer.CustomerEntity;
import com.example.ecommercedemo.exception.CustomerNotFoundException;
import com.example.ecommercedemo.exception.ErrorCode;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.model.AddressReq;
import com.example.ecommercedemo.customer.CustomerRepository;
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
  private final CustomerRepository customerRepository;
  private final AddressMapper addressMapper;

  private final static Logger LOGGER = LoggerFactory.getLogger(AddressServiceImpl.class);

  public AddressServiceImpl(AddressRepository addressRepository, CustomerRepository customerRepository, AddressMapper addressMapper) {
    this.addressRepository = addressRepository;
    this.customerRepository = customerRepository;
    this.addressMapper = addressMapper;
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

    // 1. Find the Customer
    CustomerEntity customerEntity = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));

    // 2. Map the DTO
    AddressEntity newAddress = addressMapper.addressReqToEntity(addressReq);

    // 3. Set the customerEntity for the new address
    newAddress.setCustomer(customerEntity);

    // 4. Save the new address entity
    AddressEntity savedAddress = addressRepository.save(newAddress);

    // 5. Map the saved address entity back to the model and return it
    return addressMapper.entityToModel(savedAddress);
//    return mapper.entityToModel(addressRepository.save(mapper.addressReqToEntity(addressReq)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Address> getAllAddresses() {
    return addressMapper.entityToModelList(addressRepository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Address> getAddressById(UUID AddressId) {
    return addressRepository.findById(AddressId).map(addressMapper::entityToModel);
   }

  @Override
  @Transactional(readOnly = true)
  public List<Address> getAddressesByCustomerId(UUID customerId) {
//    return customerRepository.findById(id) // Returns Optional<CustomerEntity>
//        .map(CustomerEntity::getAddresses) // Returns Optional<List<AddressEntity>>
//        .map(mapper::entityToModelList); // Returns Optional<List<Address>>

    // 1. Check if the customer exists. If not, throw CustomerNotFoundException.
    CustomerEntity customerEntity = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));

    // 2. Get the list of addresses (which is guaranteed non-null, potentially empty).
    List<AddressEntity> addressEntities = customerEntity.getAddresses();

    // 3. Map the non-null list and return it directly.
    return addressMapper.entityToModelList(addressEntities);
  }

  @Override
  @Transactional
  public void deleteAddressById(UUID AddressId) {
    addressRepository.deleteById(AddressId);
  }
}