package com.example.ecommercedemo.service;

import com.example.ecommercedemo.customer.CustomerServiceImpl;
import com.example.ecommercedemo.customer.CustomerEntity;
import com.example.ecommercedemo.exceptions.CustomerAlreadyExistsException;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.exceptions.ErrorCode;
import com.example.ecommercedemo.customer.CustomerMapper;
import com.example.ecommercedemo.model.Customer;
import com.example.ecommercedemo.model.CustomerReq;
import com.example.ecommercedemo.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private CustomerMapper customerMapper;

  @InjectMocks
  private CustomerServiceImpl customerService;

  // Argument captor to inspect the entity passed to save()
  private ArgumentCaptor<CustomerEntity> customerEntityArgumentCaptor;

  // --- Test Data ---
  private UUID customerId;
  private CustomerEntity customerEntity;
  private Customer customerModel;
  private CustomerReq customerReq;

  @BeforeEach
  void setUp() {
    customerId = UUID.randomUUID();
    customerEntityArgumentCaptor = ArgumentCaptor.forClass(CustomerEntity.class);

    // 1. Setup Request DTO
    customerReq = new CustomerReq();
    customerReq.setUsername("username");
    customerReq.setFirstName("Firstname");
    customerReq.setLastName("Lastname");
    customerReq.setEmail("email@test.com");

    // 2. Setup Entities
    customerEntity = new CustomerEntity();
    customerEntity.setId(customerId);
    customerEntity.setUsername(customerReq.getUsername());
    customerEntity.setFirstName(customerReq.getFirstName());
    customerEntity.setLastName(customerReq.getLastName());
    customerEntity.setEmail(customerReq.getEmail());

    // 2. Setup Models/DTOs
    customerModel = new Customer();
    customerModel.setId(customerId);
    customerModel.setUsername(customerReq.getUsername());
    customerModel.setFirstName(customerReq.getFirstName());
    customerModel.setLastName(customerReq.getLastName());
    customerModel.setEmail(customerReq.getEmail());
  }

  // ------------------------------------------------------------------
  // CREATE Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("CREATE: Should successfully create and return a new customer")
  void createcustomer_Success() {
    // --- Setup Mocks ---
    // 1. Username does not exist
    when(customerRepository.existsByUsername(customerReq.getUsername())).thenReturn(false);
    // 2. Repository saves the new entity and returns a mock entity with an ID
    when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);
    // 3. Mapper converts the saved entity to the final model
    when(customerMapper.entityToModel(customerEntity)).thenReturn(customerModel);

    // --- Execute ---
    Customer result = customerService.createCustomer(customerReq);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertEquals(customerModel.getUsername(), result.getUsername());

    verify(customerRepository).save(customerEntityArgumentCaptor.capture());
    CustomerEntity capturedEntity = customerEntityArgumentCaptor.getValue();
    assertEquals(customerReq.getUsername(), capturedEntity.getUsername());
    // Assert creation happened
    verify(customerRepository, times(1)).save(any(CustomerEntity.class));
  }

  @Test
  @DisplayName("CREATE: Should throw CustomerAlreadyExistsException if username exists")
  void createUser_WhenUsernameExists_ThrowsException() {
    // --- Setup Mocks ---
    when(customerRepository.existsByUsername(customerReq.getUsername())).thenReturn(true);

    // --- Execute & Assert ---
    CustomerAlreadyExistsException exception = assertThrows(
        CustomerAlreadyExistsException.class,
        () -> customerService.createCustomer(customerReq)
    );

    // --- Verify ---
    assertEquals(ErrorCode.CUSTOMER_ALREADY_EXISTS.getErrCode(), exception.getErrorCode());
    verify(customerRepository, never()).save(any());
  }

  @Test
  @DisplayName("CREATE: Should throw IllegalArgumentException if customerReq is null")
  void createUser_WhenCustomerReqIsNull_ThrowsException() {
    // --- Execute & Assert ---
    assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(null));

    // --- Verify ---
    verifyNoInteractions(customerRepository);
  }

  @Test
  @DisplayName("CREATE: Should throw IllegalArgumentException if username is null")
  void createUser_WhenUsernameIsNull_ThrowsException() {
    // --- Setup ---
    customerReq.setUsername(null);
    // --- Execute & Assert ---
    assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(customerReq));

    // --- Verify ---
    verify(customerRepository, never()).existsByUsername(any());
  }

  // ------------------------------------------------------------------
  // UPDATE Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("UPDATE: Should update customer with same username (no conflict check needed)")
  void updateCustomer_WithSameUsername_Success() {
    // --- Setup Mocks ---
    // 1. Find existing customer
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
    // 2. Repository saves the updated entity
    when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);
    // 3. Mapper converts to model
    when(customerMapper.entityToModel(customerEntity)).thenReturn(customerModel);

    // --- Execute ---
    Customer result = customerService.updateCustomer(customerId, customerReq);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertEquals(customerModel.getUsername(), result.getUsername());

    // Verify: existsByUsername was NOT called because username didn't change
    verify(customerRepository, never()).existsByUsername(any());
    // Verify: findById and save were called
    verify(customerRepository, times(1)).findById(customerId);
    verify(customerRepository, times(1)).save(customerEntity);
  }

  @Test
  @DisplayName("UPDATE: Should update customer with a new, unique username")
  void updateUser_WithNewUniqueUsername_Success() {
    // --- Setup ---
    String newUsername = "new_unique_name";
    CustomerReq newReq = new CustomerReq();
    // Copy all fields but change username
    newReq.setUsername(newUsername);
    newReq.setFirstName(customerReq.getFirstName());
    // ... set other required fields ...

    // --- Setup Mocks ---
    // 1. Find existing customer (current username: "testuser")
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
    // 2. Check for conflict (new username "new_unique_name" must be unique)
    when(customerRepository.existsByUsername(newUsername)).thenReturn(false);
    // 3. Repository saves
    CustomerEntity updatedEntity = new CustomerEntity();
    updatedEntity.setId(customerId);
    updatedEntity.setUsername(newUsername);
    when(customerRepository.save(any(CustomerEntity.class))).thenReturn(updatedEntity);
    // 4. Mapper converts
    Customer updatedModel = new Customer();
    updatedModel.setId(customerId);
    updatedModel.setUsername(newUsername);
    when(customerMapper.entityToModel(updatedEntity)).thenReturn(updatedModel);

    // --- Execute ---
    Customer result = customerService.updateCustomer(customerId, newReq);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertEquals(newUsername, result.getUsername());

    // Verify: Conflict check *was* called
    verify(customerRepository, times(1)).existsByUsername(newUsername);
    // Verify: The saved entity has the new username
    verify(customerRepository).save(customerEntityArgumentCaptor.capture());
    assertEquals(newUsername, customerEntityArgumentCaptor.getValue().getUsername());
  }

  @Test
  @DisplayName("UPDATE: Should throw CustomerAlreadyExistsException if new username conflicts")
  void updateUser_WhenNewUsernameConflicts_ThrowsException() {
    // --- Setup ---
    String conflictingUsername = "existing_user_name";
    CustomerReq newReq = new CustomerReq();
    newReq.setUsername(conflictingUsername);
    // Setup existing entity with a *different* username
    customerEntity.setUsername("original_name");

    // --- Setup Mocks ---
    // 1. Find existing customer
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
    // 2. Conflict check finds another customer with the new name
    when(customerRepository.existsByUsername(conflictingUsername)).thenReturn(true);

    // --- Execute & Assert ---
    CustomerAlreadyExistsException exception = assertThrows(
        CustomerAlreadyExistsException.class,
        () -> customerService.updateCustomer(customerId, newReq)
    );

    // --- Verify ---
    assertEquals(ErrorCode.CUSTOMER_ALREADY_EXISTS.getErrCode(), exception.getErrorCode());
    verify(customerRepository, times(1)).existsByUsername(conflictingUsername);
    verify(customerRepository, never()).save(any());
  }

  @Test
  @DisplayName("UPDATE: Should throw CustomerNotFoundException if ID not found")
  void updateUser_WhenIdNotFound_ThrowsException() {
    // --- Setup Mocks ---
    when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

    // --- Execute & Assert ---
    assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(customerId, customerReq));

    // --- Verify ---
    verify(customerRepository, never()).existsByUsername(any());
    verify(customerRepository, never()).save(any());
  }

  // ------------------------------------------------------------------
  // Query Methods Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("GET_ALL: Should return a list of all users")
  void getAllCustomers_ReturnsList() {
    // --- Setup Mocks ---
    List<CustomerEntity> entityList = List.of(customerEntity);
    List<Customer> modelList = List.of(customerModel);
    when(customerRepository.findAll()).thenReturn(entityList);
    when(customerMapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    List<Customer> result = customerService.getAllCustomers();

    // --- Assert & Verify ---
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(customerRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("GET_ALL: Should return an empty list when no users exist")
  void getAllCustomers_WhenNoCustomers_ReturnsEmptyList() {
    // --- Setup Mocks ---
    // Mock the repository to return an empty list
    when(customerRepository.findAll()).thenReturn(List.of());
    // Mock the mapper to also return an empty list
    when(customerMapper.entityToModelList(List.of())).thenReturn(List.of());

    // --- Execute ---
    List<Customer> result = customerService.getAllCustomers();

    // --- Assert & Verify ---
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(customerRepository, times(1)).findAll();
    verify(customerMapper, times(1)).entityToModelList(List.of());
  }

  @Test
  @DisplayName("GET_BY_ID: Should return Optional<Customer> when found")
  void getCustomerById_WhenFound_ReturnsOptionalUser() {
    // --- Setup Mocks ---
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
    when(customerMapper.entityToModel(customerEntity)).thenReturn(customerModel);

    // --- Execute ---
    Optional<Customer> result = customerService.getCustomerById(customerId);

    // --- Assert & Verify ---
    assertTrue(result.isPresent());
    assertEquals(customerId, result.get().getId());
    verify(customerRepository, times(1)).findById(customerId);
  }

  @Test
  @DisplayName("GET_BY_ID: Should return Optional.empty() when not found")
  void getCustomerById_WhenNotFound_ReturnsEmptyOptional() {
    // --- Setup Mocks ---
    when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

    // --- Execute ---
    Optional<Customer> result = customerService.getCustomerById(customerId);

    // --- Assert & Verify ---
    assertFalse(result.isPresent());
    verify(customerRepository, times(1)).findById(customerId);
    verify(customerMapper, never()).entityToModel(any());
  }

  // ------------------------------------------------------------------
  // deleteAddressById Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("DELETE: Should call repository deleteById once")
  void deleteCustomerById_ShouldCallRepository() {
    // --- Execute ---
    customerService.deleteCustomerById(customerId);

    // --- Assert & Verify ---
    verify(customerRepository, times(1)).deleteById(customerId);
  }

  @Test
  @DisplayName("DELETE: Should throw exception if ID does not exist")
  void deleteCustomerById_WhenIdNotFound_ShouldThrowException() {
    // --- Setup Mocks ---
    // Mock the repository to throw an exception when deleteById is called
    doThrow(new org.springframework.dao.EmptyResultDataAccessException(1))
        .when(customerRepository).deleteById(customerId);

    // --- Execute & Assert ---
    // Assert that the expected exception is thrown when the service method is called
    assertThrows(org.springframework.dao.EmptyResultDataAccessException.class, () -> {
      customerService.deleteCustomerById(customerId);
    });

    // --- Verify ---
    verify(customerRepository, times(1)).deleteById(customerId);
  }
}
