package com.example.ecommercedemo.controller;

import com.example.ecommercedemo.hateoas.AddressRepresentationModelAssembler;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.model.AddressReq;
import com.example.ecommercedemo.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 1. Specifies the controller to test and loads only the web layer components
@WebMvcTest(AddressController.class)
public class CustomerControllerTest {

  // MockMvc is auto-configured and injected for making HTTP requests
  @Autowired
  private MockMvc mockMvc;

  // ObjectMapper is useful for converting Java objects to JSON payloads
  @Autowired
  private ObjectMapper objectMapper;

  // 2. Mocks the service layer dependency
  @MockBean
  private AddressService mockAddressService;

  // 3. Mocks the HATEOAS assembler dependency
  // NOTE: You would typically mock the assembler, but a simpler approach
  // is often to configure it to just return the input object for simplicity
  // or provide a simple implementation for testing purposes. We'll mock it here.
  @MockBean
  private AddressRepresentationModelAssembler mockAssembler;

  private static final String BASE_URL = "/api/v1/addresses";
  private final UUID customerId = UUID.randomUUID();
  private final UUID addressId = UUID.randomUUID();

  // --- Test Data Helpers ---
  private Address createTestAddress() {
    return new Address().id(addressId).street("123 Test St").city("Testville");
  }

  private AddressReq createTestAddressReq() {
    return new AddressReq().street("123 Test St").city("Testville").zipcode("12345");
  }

  // --- Test Cases ---

  @Test
  void createAddress_ShouldReturn201Created() throws Exception {
    AddressReq request = createTestAddressReq();
    Address createdAddress = createTestAddress();

    // Mock the Service and Assembler behavior
    when(mockAddressService.createAddress(any(UUID.class), any(AddressReq.class)))
        .thenReturn(createdAddress);
    when(mockAssembler.toModel(any(Address.class)))
        .thenReturn(createdAddress); // Assembler returns the Address with links

    mockMvc.perform(post("/api/v1/customers/{id}/addresses", customerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(addressId.toString()))
        .andExpect(jsonPath("$.street").value("123 Test St"));
  }

  // ---

  @Test
  void getAddressById_ShouldReturn200Ok() throws Exception {
    Address address = createTestAddress();

    // Mock the Service and Assembler behavior
    when(mockAddressService.getAddressById(addressId))
        .thenReturn(Optional.of(address));
    when(mockAssembler.toModel(address))
        .thenReturn(address);

    mockMvc.perform(get(BASE_URL + "/{uuid}", addressId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(addressId.toString()));
  }

  @Test
  void getAddressById_ShouldReturn404NotFound() throws Exception {
    // Mock the Service behavior to return empty
    when(mockAddressService.getAddressById(addressId))
        .thenReturn(Optional.empty());

    mockMvc.perform(get(BASE_URL + "/{uuid}", addressId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  // ---

  @Test
  void getCustomerAddresses_ShouldReturnListOfAddresses() throws Exception {
    List<Address> addresses = Collections.singletonList(createTestAddress());

    // Mock the Service and Assembler behavior
    when(mockAddressService.getAddressesByCustomerId(customerId))
        .thenReturn(addresses);
    when(mockAssembler.toModelList(addresses))
        .thenReturn(addresses); // Assembler returns the list with links

    mockMvc.perform(get("/api/v1/customers/{id}/addresses", customerId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(addressId.toString()))
        .andExpect(jsonPath("$.length()").value(1));
  }

  // ---

  @Test
  void deleteAddressById_ShouldReturn202Accepted() throws Exception {
    // Mock the Service behavior (it returns void/nothing)
    doNothing().when(mockAddressService).deleteAddressById(addressId);

    mockMvc.perform(delete(BASE_URL + "/{uuid}", addressId))
        .andExpect(status().isAccepted());
  }
}