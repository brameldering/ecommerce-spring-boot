package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.AddressEntity;
import com.example.ecommercedemo.entity.OrderEntity;
import com.example.ecommercedemo.entity.ShipmentEntity;
import com.example.ecommercedemo.entity.UserEntity;
import com.example.ecommercedemo.mappers.ShipmentMapper;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.model.Shipment;
import com.example.ecommercedemo.model.ShipmentReq;
import com.example.ecommercedemo.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceTest {

  @Mock
  private ShipmentRepository shipmentRepository;

  @Mock
  private ShipmentMapper mapper;

  @InjectMocks
  private ShipmentServiceImpl shipmentService;

  // --- Test Data ---
  private UUID shipmentId;
  private UUID orderId;
  private UUID customerId;
  private UUID addressId;
  private OrderEntity orderEntity;
  private UserEntity userEntity;
  private AddressEntity addressEntity;
  private Address addressModel;
  private ShipmentEntity shipmentEntity;
  private Shipment shipmentModel;
  private ShipmentReq shipmentReq;

  @BeforeEach
  void setUp() {
    shipmentId = UUID.randomUUID();
    orderId = UUID.randomUUID();
    addressId = UUID.randomUUID();
    customerId = UUID.randomUUID();

    // 1. Setup Entities
    userEntity = new UserEntity();
    userEntity.setId(customerId);

    addressEntity = new AddressEntity();
    addressEntity.setId(addressId);
    addressEntity.setStreet("123 Main St");
    addressEntity.setUser(userEntity);

    orderEntity = new OrderEntity();
    orderEntity.setId(orderId);
    orderEntity.setShipment(shipmentEntity);
    orderEntity.setUserEntity(userEntity);
    orderEntity.setAddressEntity(addressEntity);

    shipmentEntity = new ShipmentEntity();
    shipmentEntity.setId(shipmentId);
    shipmentEntity.setCarrier("Carrier");
    shipmentEntity.setOrderEntity(orderEntity);

    // 2. Setup Models/DTOs
    addressModel = new Address();
    addressModel.setId(addressId);
    addressModel.setStreet("123 Main St");
    addressModel.setUserId(customerId);

    shipmentModel = new Shipment();
    shipmentModel.setId(shipmentId);
    shipmentModel.setCarrier("Carrier");

    shipmentReq = new ShipmentReq();
  }

  // ------------------------------------------------------------------
  // getShipment Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("GET_BY_ORDER_ID: Should return List<Shipment> when order has shipments")
  void getShipmentsByOrderId_WhenShipmentsForOrderExists_ReturnsList() {
    // --- Setup Mocks ---
    List<ShipmentEntity> entityList = List.of(shipmentEntity);
    List<Shipment> modelList = List.of(shipmentModel);

    // Mock the repository
    when(shipmentRepository.findByOrderEntity_Id(orderId))
        .thenReturn(entityList);

    // Mock the mapper
    when(mapper.entityToModelList(entityList)).thenReturn(modelList);

    // --- Execute ---
    List<Shipment> result = shipmentService.getShipmentsByOrderId(orderId);

    // --- Assert & Verify ---
    // 1. Assert the list is not null (though the service guarantees this)
    assertNotNull(result);

    // 2. Assert the list is not empty
    assertFalse(result.isEmpty());

    // 3. Assert the size
    assertEquals(1, result.size());

    // 4. Verify the repository call (The implementation should now call findById)
    verify(shipmentRepository, times(1)).findByOrderEntity_Id(orderId);
    verify(shipmentRepository, never()).findById(any());
  }

  @Test
  @DisplayName("GET_BY_ORDER_ID: Should return empty List<Shipment> when order has no shipments")
  void getShipmentsByOrderId_WhenOrderHasNoShipments_ReturnsEmptyList() {
    // --- Setup Mocks ---
    List<ShipmentEntity> emptyEntityList = Collections.emptyList();
    List<Shipment> emptyModelList = Collections.emptyList();

    // Mock the repository
    when(shipmentRepository.findByOrderEntity_Id(orderId))
        .thenReturn(emptyEntityList);

    when(mapper.entityToModelList(emptyEntityList)).thenReturn(emptyModelList);

    // --- Execute ---
    List<Shipment> result = shipmentService.getShipmentsByOrderId(orderId);

    // --- Assert & Verify ---
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(shipmentRepository, times(1)).findByOrderEntity_Id(orderId);
    verify(shipmentRepository, never()).findById(any());
  }

  // ------------------------------------------------------------------
  // shipOrder Tests
  // ------------------------------------------------------------------

  @Test
  @DisplayName("shipOrder: Should return null as the method is currently unimplemented")
  void shipOrder_ReturnsNull() {
    // --- Execute ---
    Shipment result = shipmentService.shipOrder(orderId, shipmentReq);

    // --- Assert & Verify ---
    assertNull(result);
    verifyNoInteractions(shipmentRepository, mapper);
  }
}
