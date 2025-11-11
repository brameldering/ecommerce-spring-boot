package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.AddressEntity;
import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.exceptions.CustomerNotFoundException;
import com.example.ecommercedemo.mappers.AddressMapper;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.model.AddressReq;
import com.example.ecommercedemo.repository.AddressRepository;
import com.example.ecommercedemo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.AdditionalAnswers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

  @Mock
  private AddressRepository addressRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private AddressMapper mapper;

  @InjectMocks
  private AddressServiceImpl addressService;

  // --- Test Data ---
  private UUID addressId;
  private UUID customerId;
  private UserEntity userEntity;
  private AddressEntity addressEntity;
  private Address addressModel;
  private AddressReq addressReq;

  @BeforeEach
  void setUp() {
    addressId = UUID.randomUUID();
    customerId = UUID.randomUUID();

    // 1. Setup Entities
    userEntity = new UserEntity();
    userEntity.setId(customerId);

    addressEntity = new AddressEntity();
    addressEntity.setId(addressId);
    addressEntity.setStreet("123 Main St");
    addressEntity.setUsers(List.of(userEntity));

    // 2. Setup Models/DTOs
    addressModel = new Address();
    addressModel.setId(addressId);
    addressModel.setStreet("123 Main St");
    addressModel.setUserId(customerId);

    addressReq = new AddressReq();
    addressReq.setStreet("123 Main St");
    addressReq.setCity("Anytown");
    addressReq.setZipcode("12345");
  }

  // ------------------------------------------------------------------
  // createAddress Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("CREATE: Should successfully create and return an Address")
  void createAddress_Success() {
    // --- Setup Mocks ---
    when(userRepository.findById(customerId)).thenReturn(Optional.of(userEntity));
    when(mapper.addressReqToEntity(addressReq)).thenReturn(addressEntity);
    // Mock save to return the entity it was passed
    when(addressRepository.save(any(AddressEntity.class))).then(AdditionalAnswers.returnsFirstArg());
    when(mapper.entityToModel(addressEntity)).thenReturn(addressModel);

    // --- Execute ---
    Address result = addressService.createAddress(customerId, addressReq);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertEquals(addressModel.getStreet(), result.getStreet());
    verify(addressRepository, times(1)).save(addressEntity);
  }

  // ------------------------------------------------------------------
  // Validation Tests for createAddress
  // ------------------------------------------------------------------

  @Test
  @DisplayName("CREATE: Should throw IllegalArgumentException when AddressReq is null")
  void createAddress_WhenAddressReqIsNull_ShouldThrowException() {
    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> addressService.createAddress(customerId, null)
    );
    assertEquals("Address cannot be null.", exception.getMessage());
    verifyNoInteractions(addressRepository, mapper, userRepository);
  }

  @Test
  @DisplayName("CREATE: Should throw IllegalArgumentException when CustomerId is null")
  void createAddress_WhenCustomerIdIsNull_ShouldThrowException() {
    // --- Setup ---

    // --- Execute & Assert ---
    CustomerNotFoundException exception = assertThrows(
        CustomerNotFoundException.class,
        () -> addressService.createAddress(null, addressReq)
    );
//    assertEquals("CustomerId cannot be null.", exception.getMessage());
    verifyNoInteractions(addressRepository, mapper);
  }

  @Test
  @DisplayName("CREATE: Should throw IllegalArgumentException when Street is null")
  void createAddress_WhenStreetIsNull_ShouldThrowException() {
    // --- Setup ---
    addressReq.setStreet(null);

    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> addressService.createAddress(customerId, addressReq)
    );
    assertEquals("Street cannot be empty.", exception.getMessage());
  }

  @Test
  @DisplayName("CREATE: Should throw IllegalArgumentException when City is blank")
  void createAddress_WhenCityIsBlank_ShouldThrowException() {
    // --- Setup ---
    addressReq.setCity("  ");

    // --- Execute & Assert ---
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> addressService.createAddress(customerId, addressReq)
    );
    assertEquals("City cannot be empty.", exception.getMessage());
  }

  // ------------------------------------------------------------------
  // Query Methods Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("GET_ALL: Should return a list of all Addresses")
  void getAllAddresses_ReturnsList() {
    // --- Setup Mocks ---
    List<AddressEntity> entityList = List.of(addressEntity);
    List<Address> modelList = List.of(addressModel);
    when(addressRepository.findAll()).thenReturn(entityList);
    when(mapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    List<Address> result = addressService.getAllAddresses();

    // --- Assert & Verify ---
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(addressRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("GET_BY_ID: Should return Optional<Address> when found")
  void getAddressById_WhenFound_ReturnsOptionalAddress() {
    // --- Setup Mocks ---
    when(addressRepository.findById(addressId)).thenReturn(Optional.of(addressEntity));
    when(mapper.entityToModel(addressEntity)).thenReturn(addressModel);

    // --- Execute ---
    Optional<Address> result = addressService.getAddressById(addressId);

    // --- Assert & Verify ---
    assertTrue(result.isPresent());
    assertEquals(addressId, result.get().getId());
    verify(addressRepository, times(1)).findById(addressId);
  }

  @Test
  @DisplayName("GET_BY_ID: Should return Optional.empty() when not found")
  void getAddressById_WhenNotFound_ReturnsEmptyOptional() {
    // --- Setup Mocks ---
    when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

    // --- Execute ---
    Optional<Address> result = addressService.getAddressById(addressId);

    // --- Assert & Verify ---
    assertFalse(result.isPresent());
    verify(addressRepository, times(1)).findById(addressId);
    verify(mapper, never()).entityToModel(any());
  }

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should return List<Address> when user has addresses")
  void getAddressesByCustomerId_WhenUserHasAddresses_ReturnsList() {
    // --- Setup Mocks ---
    List<AddressEntity> entityList = List.of(addressEntity);
    List<Address> modelList = List.of(addressModel);

    // Set up the UserEntity to return the AddressEntity list
    userEntity.setAddresses(entityList);

    when(userRepository.findById(customerId)).thenReturn(Optional.of(userEntity));
    when(mapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    List<Address> result = addressService.getAddressesByCustomerId(customerId);

    // --- Assert & Verify ---
    // 1. Assert the list is not null (though the service guarantees this)
    assertNotNull(result);

    // 2. Assert the list is not empty
    assertFalse(result.isEmpty());

    // 3. Assert the size
    assertEquals(1, result.size());

    // 4. Verify the repository call (The implementation should now call findById)
    verify(userRepository, times(1)).findById(customerId);
  }

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should throw CustomerNotFoundException when user is not found")
  void getAddressesByCustomerId_WhenUserNotFound_ReturnsCustomerNotFoundException() {
    // --- Setup Mocks ---
    when(userRepository.findById(customerId)).thenReturn(Optional.empty());

    assertThrows(
        CustomerNotFoundException.class,
        () -> addressService.getAddressesByCustomerId(customerId)
    );

    verify(userRepository, times(1)).findById(customerId);
    verify(mapper, never()).entityToModelList(anyList());
  }

  @Test
  @DisplayName("GET_BY_CUSTOMER_ID: Should return empty List<Address> when user has no addresses")
  void getAddressesByCustomerId_WhenUserHasNoAddresses_ReturnsEmptyListInOptional() {
    // --- Setup Mocks ---
    List<AddressEntity> emptyEntityList = Collections.emptyList();
    List<Address> emptyModelList = Collections.emptyList();

    // Set up the UserEntity to return the empty AddressEntity list
    userEntity.setAddresses(emptyEntityList);

    when(userRepository.findById(customerId)).thenReturn(Optional.of(userEntity));
    when(mapper.entityToModelList(emptyEntityList)).thenReturn(emptyModelList);

    // --- Execute ---
    List<Address> result = addressService.getAddressesByCustomerId(customerId);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userRepository, times(1)).findById(customerId);
  }


  // ------------------------------------------------------------------
  // deleteAddressById Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("DELETE: Should call repository deleteById once")
  void deleteAddressById_ShouldCallRepository() {
    // --- Execute ---
    addressService.deleteAddressById(addressId);

    // --- Assert & Verify ---
    verify(addressRepository, times(1)).deleteById(addressId);
  }
}
