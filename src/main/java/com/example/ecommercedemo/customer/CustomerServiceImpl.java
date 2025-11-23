package com.example.ecommercedemo.customer;

import com.example.ecommercedemo.exception.CustomerAlreadyExistsException;
import com.example.ecommercedemo.exception.CustomerNotFoundException;
import com.example.ecommercedemo.exception.ErrorCode;
import com.example.ecommercedemo.model.Customer;
import com.example.ecommercedemo.model.CustomerReq;
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

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
    this.customerRepository = customerRepository;
    this.customerMapper = customerMapper;
  }

  @Override
  @Transactional
  public Customer createCustomer(@Valid CustomerReq customerReq) {
    if (customerReq == null) {
      throw new IllegalArgumentException("CustomerReq cannot be null");
    }
    if (customerReq.getUsername() == null || customerReq.getUsername().isEmpty()) {
      throw new IllegalArgumentException("UserName cannot be null");
    }
    if (customerRepository.existsByUsername(customerReq.getUsername())) {
      throw new CustomerAlreadyExistsException(ErrorCode.CUSTOMER_ALREADY_EXISTS);
    }

    CustomerEntity customerEntity = new CustomerEntity()
        .setUsername(customerReq.getUsername())
        .setFirstName(customerReq.getFirstName())
        .setLastName(customerReq.getLastName())
        .setEmail(customerReq.getEmail())
        .setPhone(customerReq.getPhone())
        .setStatus(customerReq.getStatus());

    CustomerEntity savedCustomer = customerRepository.save(customerEntity);
    return customerMapper.entityToModel(savedCustomer);
  }

  @Override
  @Transactional
  public Customer updateCustomer(@NotNull(message = "Customer UUID cannot be null.") UUID customerId, @Valid CustomerReq customerReq) {
    if (customerReq == null) {
      throw new IllegalArgumentException("Body cannot be null");
    }
    if (customerReq.getUsername() == null || customerReq.getUsername().isEmpty()) {
      throw new IllegalArgumentException("UserName cannot be null");
    }

    // 1.  Check if customer exists and retrieve existing customer
    CustomerEntity existingCustomerEntity = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));

    // 2. Check for Username Conflict
    String newUsername = customerReq.getUsername();
    // Check 2a: Only perform a uniqueness check if the username has actually changed.
    if (!existingCustomerEntity.getUsername().equals(newUsername)) {
      // Check 2b: If the new username exists in the repository (i.e., belongs to someone else)
      if (customerRepository.existsByUsername(newUsername)) {
        throw new CustomerAlreadyExistsException(ErrorCode.CUSTOMER_ALREADY_EXISTS);
      }
    }

    existingCustomerEntity
        .setUsername(customerReq.getUsername())
        .setFirstName(customerReq.getFirstName())
        .setLastName(customerReq.getLastName())
        .setEmail(customerReq.getEmail())
        .setPhone(customerReq.getPhone())
        .setStatus(customerReq.getStatus());

    CustomerEntity updatedCustomer = customerRepository.save(existingCustomerEntity);
    return customerMapper.entityToModel(updatedCustomer);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Customer> getAllCustomers() {
    List<CustomerEntity> entities = customerRepository.findAll();
    return customerMapper.entityToModelList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Customer> getCustomerById(UUID id) {
    return customerRepository.findById(id)
        .map(customerMapper::entityToModel);
  }

  @Override
  @Transactional
  public void deleteCustomerById(UUID id) {
    customerRepository.deleteById(id);
  }
}

