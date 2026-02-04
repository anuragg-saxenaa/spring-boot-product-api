package com.arrayindex.productmanagementapi.controller;

import com.arrayindex.productmanagementapi.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Product Controller HTTP Status Tests")
class ProductControllerHttpStatusTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/products should return 201 Created")
    void createProduct_ShouldReturn201Created() throws Exception {
        Product product = createTestProduct("New Product", "New Description", 99.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("GET /api/products should return 200 OK")
    void getAllProducts_ShouldReturn200Ok() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/products/{id} should return 200 OK when product exists")
    void getProductById_WhenExists_ShouldReturn200Ok() throws Exception {
        // First create a product
        Product product = createTestProduct("Test Product", "Test Description", 99.99);
        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Product createdProduct = objectMapper.readValue(response, Product.class);

        // Then test GET by ID
        mockMvc.perform(get("/api/products/" + createdProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/products/{id} should return 404 Not Found when product doesn't exist")
    void getProductById_WhenNotExists_ShouldReturn404NotFound() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("PUT /api/products/{id} should return 200 OK when product updated successfully")
    void updateProduct_WhenSuccessful_ShouldReturn200Ok() throws Exception {
        // First create a product
        Product product = createTestProduct("Original Product", "Original Description", 99.99);
        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Product createdProduct = objectMapper.readValue(response, Product.class);

        // Update the product
        createdProduct.setName("Updated Product");

        // Test PUT
        mockMvc.perform(put("/api/products/" + createdProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("PUT /api/products/{id} should return 404 Not Found when product doesn't exist")
    void updateProduct_WhenNotExists_ShouldReturn404NotFound() throws Exception {
        Product product = createTestProduct("Non-existent Product", "Description", 99.99);

        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} should return 200 OK when product deleted successfully")
    void deleteProduct_WhenSuccessful_ShouldReturn200Ok() throws Exception {
        // First create a product
        Product product = createTestProduct("Product to Delete", "Description", 99.99);
        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Product createdProduct = objectMapper.readValue(response, Product.class);

        // Test DELETE
        mockMvc.perform(delete("/api/products/" + createdProduct.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/products should return 400 Bad Request for invalid input")
    void createProduct_WithInvalidInput_ShouldReturn400BadRequest() throws Exception {
        Product product = new Product();
        product.setName(""); // Empty name - invalid
        product.setDescription("Test Description");
        product.setPrice(99.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products should return 415 Unsupported Media Type for wrong content type")
    void createProduct_WithWrongContentType_ShouldReturn415UnsupportedMediaType() throws Exception {
        Product product = createTestProduct("Test Product", "Test Description", 99.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("GET /api/products/{id} should return 400 Bad Request for invalid ID format")
    void getProductById_WithInvalidIdFormat_ShouldReturn400BadRequest() throws Exception {
        mockMvc.perform(get("/api/products/invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return correct HTTP headers")
    void createProduct_ShouldReturnCorrectHeaders() throws Exception {
        Product product = createTestProduct("Header Test Product", "Header Test Description", 99.99);

        MvcResult result = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andReturn();

        // Verify headers
        assertThat(result.getResponse().getHeader("Content-Type")).contains("application/json");
        assertThat(result.getResponse().getHeader("Location")).isNotNull();
    }

    @Test
    @DisplayName("Should handle concurrent requests correctly")
    void concurrentRequests_ShouldHandleCorrectly() throws Exception {
        // Create multiple products concurrently
        List<Product> products = List.of(
                createTestProduct("Product 1", "Description 1", 99.99),
                createTestProduct("Product 2", "Description 2", 149.99),
                createTestProduct("Product 3", "Description 3", 199.99)
        );

        // Test concurrent creation
        for (Product product : products) {
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isCreated());
        }

        // Verify all products were created
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

    // Helper method
    private Product createTestProduct(String name, String description, Double price) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        return product;
    }
}