package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.mappers.UserMapper;
import com.example.ecommercedemo.model.User;
import com.example.ecommercedemo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper mapper;

  @InjectMocks
  private UserServiceImpl userService;

  // --- Test Data ---
  private UUID customerId;
  private UserEntity userEntity;
  private User userModel;

  @BeforeEach
  void setUp() {
    customerId = UUID.randomUUID();

    // 1. Setup Entities
    userEntity = new UserEntity();
    userEntity.setId(customerId);

    // 2. Setup Models/DTOs
    userModel = new User();
    userModel.setId(customerId);
    userModel.setFirstName("FirstName");
    userModel.setLastName("LastName");
    userModel.setEmail("email@test.com");
    userModel.setUsername("username");
  }

  // ------------------------------------------------------------------
  // Query Methods Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("GET_ALL: Should return a list of all users")
  void getAllCustomers_ReturnsList() {
    // --- Setup Mocks ---
    List<UserEntity> entityList = List.of(userEntity);
    List<User> modelList = List.of(userModel);
    when(userRepository.findAll()).thenReturn(entityList);
    when(mapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    List<User> result = userService.getAllCustomers();

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
    List<User> result = userService.getAllCustomers();

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
    when(mapper.entityToModel(userEntity)).thenReturn(userModel);

    // --- Execute ---
    Optional<User> result = userService.getCustomerById(customerId);

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
    Optional<User> result = userService.getCustomerById(customerId);

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
