package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.exceptions.ErrorCode;
import com.example.ecommercedemo.exceptions.GenericAlreadyExistsException;
import com.example.ecommercedemo.mappers.UserMapper;
import com.example.ecommercedemo.model.Customer;
import com.example.ecommercedemo.model.CustomerReq;
import com.example.ecommercedemo.repository.UserRepository;
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
  private UserRepository userRepository;

  @Mock
  private UserMapper mapper;

  @InjectMocks
  private CustomerServiceImpl userService;

  // Argument captor to inspect the entity passed to save()
  private ArgumentCaptor<UserEntity> userEntityCaptor;

  // --- Test Data ---
  private UUID customerId;
  private UserEntity userEntity;
  private Customer customerModel;
  private CustomerReq customerReq;

  @BeforeEach
  void setUp() {
    customerId = UUID.randomUUID();
    userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);

    // 1. Setup Request DTO
    customerReq = new CustomerReq();
    customerReq.setUsername("username");
    customerReq.setFirstName("Firstname");
    customerReq.setLastName("Lastname");
    customerReq.setEmail("email@test.com");

    // 2. Setup Entities
    userEntity = new UserEntity();
    userEntity.setId(customerId);
    userEntity.setUsername(customerReq.getUsername());
    userEntity.setFirstName(customerReq.getFirstName());
    userEntity.setLastName(customerReq.getLastName());
    userEntity.setEmail(customerReq.getEmail());

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
  @DisplayName("CREATE: Should successfully create and return a new user")
  void createUser_Success() {
    // --- Setup Mocks ---
    // 1. Username does not exist
    when(userRepository.existsByUsername(customerReq.getUsername())).thenReturn(false);
    // 2. Repository saves the new entity and returns a mock entity with an ID
    when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
    // 3. Mapper converts the saved entity to the final model
    when(mapper.entityToModel(userEntity)).thenReturn(customerModel);

    // --- Execute ---
    Customer result = userService.createUser(customerReq);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertEquals(customerModel.getUsername(), result.getUsername());

    verify(userRepository).save(userEntityCaptor.capture());
    UserEntity capturedEntity = userEntityCaptor.getValue();
    assertEquals(customerReq.getUsername(), capturedEntity.getUsername());
    // Assert creation happened
    verify(userRepository, times(1)).save(any(UserEntity.class));
  }

  @Test
  @DisplayName("CREATE: Should throw GenericAlreadyExistsException if username exists")
  void createUser_WhenUsernameExists_ThrowsException() {
    // --- Setup Mocks ---
    when(userRepository.existsByUsername(customerReq.getUsername())).thenReturn(true);

    // --- Execute & Assert ---
    GenericAlreadyExistsException exception = assertThrows(
        GenericAlreadyExistsException.class,
        () -> userService.createUser(customerReq)
    );

    // --- Verify ---
    assertEquals(ErrorCode.GENERIC_ALREADY_EXISTS.getErrCode(), exception.getErrorCode());
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("CREATE: Should throw IllegalArgumentException if UserReq is null")
  void createUser_WhenUserReqIsNull_ThrowsException() {
    // --- Execute & Assert ---
    assertThrows(IllegalArgumentException.class, () -> userService.createUser(null));

    // --- Verify ---
    verifyNoInteractions(userRepository);
  }

  @Test
  @DisplayName("CREATE: Should throw IllegalArgumentException if username is null")
  void createUser_WhenUsernameIsNull_ThrowsException() {
    // --- Setup ---
    customerReq.setUsername(null);
    // --- Execute & Assert ---
    assertThrows(IllegalArgumentException.class, () -> userService.createUser(customerReq));

    // --- Verify ---
    verify(userRepository, never()).existsByUsername(any());
  }

  // ------------------------------------------------------------------
  // UPDATE Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("UPDATE: Should update user with same username (no conflict check needed)")
  void updateUser_WithSameUsername_Success() {
    // --- Setup Mocks ---
    // 1. Find existing user
    when(userRepository.findById(customerId)).thenReturn(Optional.of(userEntity));
    // 2. Repository saves the updated entity
    when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
    // 3. Mapper converts to model
    when(mapper.entityToModel(userEntity)).thenReturn(customerModel);

    // --- Execute ---
    Customer result = userService.updateUser(customerId, customerReq);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertEquals(customerModel.getUsername(), result.getUsername());

    // Verify: existsByUsername was NOT called because username didn't change
    verify(userRepository, never()).existsByUsername(any());
    // Verify: findById and save were called
    verify(userRepository, times(1)).findById(customerId);
    verify(userRepository, times(1)).save(userEntity);
  }

  @Test
  @DisplayName("UPDATE: Should update user with a new, unique username")
  void updateUser_WithNewUniqueUsername_Success() {
    // --- Setup ---
    String newUsername = "new_unique_name";
    CustomerReq newReq = new CustomerReq();
    // Copy all fields but change username
    newReq.setUsername(newUsername);
    newReq.setFirstName(customerReq.getFirstName());
    // ... set other required fields ...

    // --- Setup Mocks ---
    // 1. Find existing user (current username: "testuser")
    when(userRepository.findById(customerId)).thenReturn(Optional.of(userEntity));
    // 2. Check for conflict (new username "new_unique_name" must be unique)
    when(userRepository.existsByUsername(newUsername)).thenReturn(false);
    // 3. Repository saves
    UserEntity updatedEntity = new UserEntity();
    updatedEntity.setId(customerId);
    updatedEntity.setUsername(newUsername);
    when(userRepository.save(any(UserEntity.class))).thenReturn(updatedEntity);
    // 4. Mapper converts
    Customer updatedModel = new Customer();
    updatedModel.setId(customerId);
    updatedModel.setUsername(newUsername);
    when(mapper.entityToModel(updatedEntity)).thenReturn(updatedModel);

    // --- Execute ---
    Customer result = userService.updateUser(customerId, newReq);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertEquals(newUsername, result.getUsername());

    // Verify: Conflict check *was* called
    verify(userRepository, times(1)).existsByUsername(newUsername);
    // Verify: The saved entity has the new username
    verify(userRepository).save(userEntityCaptor.capture());
    assertEquals(newUsername, userEntityCaptor.getValue().getUsername());
  }

  @Test
  @DisplayName("UPDATE: Should throw GenericAlreadyExistsException if new username conflicts")
  void updateUser_WhenNewUsernameConflicts_ThrowsException() {
    // --- Setup ---
    String conflictingUsername = "existing_user_name";
    CustomerReq newReq = new CustomerReq();
    newReq.setUsername(conflictingUsername);
    // Setup existing entity with a *different* username
    userEntity.setUsername("original_name");

    // --- Setup Mocks ---
    // 1. Find existing user
    when(userRepository.findById(customerId)).thenReturn(Optional.of(userEntity));
    // 2. Conflict check finds another user with the new name
    when(userRepository.existsByUsername(conflictingUsername)).thenReturn(true);

    // --- Execute & Assert ---
    GenericAlreadyExistsException exception = assertThrows(
        GenericAlreadyExistsException.class,
        () -> userService.updateUser(customerId, newReq)
    );

    // --- Verify ---
    assertEquals(ErrorCode.GENERIC_ALREADY_EXISTS.getErrCode(), exception.getErrorCode());
    verify(userRepository, times(1)).existsByUsername(conflictingUsername);
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("UPDATE: Should throw CustomerNotFoundException if ID not found")
  void updateUser_WhenIdNotFound_ThrowsException() {
    // --- Setup Mocks ---
    when(userRepository.findById(customerId)).thenReturn(Optional.empty());

    // --- Execute & Assert ---
    assertThrows(CustomerNotFoundException.class, () -> userService.updateUser(customerId, customerReq));

    // --- Verify ---
    verify(userRepository, never()).existsByUsername(any());
    verify(userRepository, never()).save(any());
  }

  // ------------------------------------------------------------------
  // Query Methods Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("GET_ALL: Should return a list of all users")
  void getAllCustomers_ReturnsList() {
    // --- Setup Mocks ---
    List<UserEntity> entityList = List.of(userEntity);
    List<Customer> modelList = List.of(customerModel);
    when(userRepository.findAll()).thenReturn(entityList);
    when(mapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    List<Customer> result = userService.getAllCustomers();

    // --- Assert & Verify ---
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(userRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("GET_ALL: Should return an empty list when no users exist")
  void getAllCustomers_WhenNoCustomers_ReturnsEmptyList() {
    // --- Setup Mocks ---
    // Mock the repository to return an empty list
    when(userRepository.findAll()).thenReturn(List.of());
    // Mock the mapper to also return an empty list
    when(mapper.entityToModelList(List.of())).thenReturn(List.of());

    // --- Execute ---
    List<Customer> result = userService.getAllCustomers();

    // --- Assert & Verify ---
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userRepository, times(1)).findAll();
    verify(mapper, times(1)).entityToModelList(List.of());
  }

  @Test
  @DisplayName("GET_BY_ID: Should return Optional<User> when found")
  void getCustomerById_WhenFound_ReturnsOptionalUser() {
    // --- Setup Mocks ---
    when(userRepository.findById(customerId)).thenReturn(Optional.of(userEntity));
    when(mapper.entityToModel(userEntity)).thenReturn(customerModel);

    // --- Execute ---
    Optional<Customer> result = userService.getCustomerById(customerId);

    // --- Assert & Verify ---
    assertTrue(result.isPresent());
    assertEquals(customerId, result.get().getId());
    verify(userRepository, times(1)).findById(customerId);
  }

  @Test
  @DisplayName("GET_BY_ID: Should return Optional.empty() when not found")
  void getCustomerById_WhenNotFound_ReturnsEmptyOptional() {
    // --- Setup Mocks ---
    when(userRepository.findById(customerId)).thenReturn(Optional.empty());

    // --- Execute ---
    Optional<Customer> result = userService.getCustomerById(customerId);

    // --- Assert & Verify ---
    assertFalse(result.isPresent());
    verify(userRepository, times(1)).findById(customerId);
    verify(mapper, never()).entityToModel(any());
  }

  // ------------------------------------------------------------------
  // deleteAddressById Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("DELETE: Should call repository deleteById once")
  void deleteCustomerById_ShouldCallRepository() {
    // --- Execute ---
    userService.deleteCustomerById(customerId);

    // --- Assert & Verify ---
    verify(userRepository, times(1)).deleteById(customerId);
  }

  @Test
  @DisplayName("DELETE: Should throw exception if ID does not exist")
  void deleteCustomerById_WhenIdNotFound_ShouldThrowException() {
    // --- Setup Mocks ---
    // Mock the repository to throw an exception when deleteById is called
    doThrow(new org.springframework.dao.EmptyResultDataAccessException(1))
        .when(userRepository).deleteById(customerId);

    // --- Execute & Assert ---
    // Assert that the expected exception is thrown when the service method is called
    assertThrows(org.springframework.dao.EmptyResultDataAccessException.class, () -> {
      userService.deleteCustomerById(customerId);
    });

    // --- Verify ---
    verify(userRepository, times(1)).deleteById(customerId);
  }
}
