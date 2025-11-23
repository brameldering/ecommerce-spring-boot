package com.example.ecommercedemo.product;

import com.example.ecommercedemo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@WithMockUser(username = "testuser")
public class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ProductService productService;

  @MockBean
  private JwtDecoder jwtDecoder;

  private final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private Product mockProduct;
  private List<Product> mockProductList;

  @BeforeEach
  void setUp() {
    // Setup mock data
    mockProduct = new Product()
        .id(PRODUCT_ID)
        .name("Laptop Pro")
        .price("1200.00")
        .count(15);

    mockProductList = List.of(
        mockProduct,
        new Product().id(UUID.randomUUID()).name("Mouse Wireless").price("25.00").count(50)
    );
  }

  // --- 1. GET /products (Query Products) ---
  @Test
  void queryProducts_shouldReturn200Ok_andListOfProducts() throws Exception {
    // Arrange
    // NOTE: The controller ignores the query parameters (tag, name, page, size)
    // and always calls productService.getAllProducts(). We mock this call.
    when(productService.getAllProducts()).thenReturn(mockProductList);

    // Act & Assert
    mockMvc.perform(get("/api/v1/products")
            .param("page", "0") // Test with optional parameter
            .param("size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // Expect 200 OK
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("Laptop Pro"));

    // Verify that the service method called matches the controller implementation
    verify(productService, times(1)).getAllProducts();
  }

  @Test
  void queryProducts_shouldReturn200Ok_andEmptyList() throws Exception {
    // Arrange
    when(productService.getAllProducts()).thenReturn(List.of());

    // Act & Assert
    mockMvc.perform(get("/api/v1/products")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  // --- 2. GET /products/{id} (Get Product by ID) ---
  @Test
  void getProductById_shouldReturn200Ok_whenProductExists() throws Exception {
    // Arrange
    when(productService.getProductById(PRODUCT_ID)).thenReturn(Optional.of(mockProduct));

    // Act & Assert
    mockMvc.perform(get("/api/v1/products/{id}", PRODUCT_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // Expect 200 OK
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(PRODUCT_ID.toString()))
        .andExpect(jsonPath("$.price").value("1200.00"));

    verify(productService, times(1)).getProductById(PRODUCT_ID);
  }

  @Test
  void getProductById_shouldReturn404NotFound_whenProductDoesNotExist() throws Exception {
    // Arrange
    when(productService.getProductById(PRODUCT_ID)).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(get("/api/v1/products/{id}", PRODUCT_ID)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound()) // Expect 404 NOT FOUND
        .andExpect(content().string(""));

    verify(productService, times(1)).getProductById(PRODUCT_ID);
  }
}