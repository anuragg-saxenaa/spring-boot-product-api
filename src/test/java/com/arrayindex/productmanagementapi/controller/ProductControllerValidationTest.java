package com.arrayindex.productmanagementapi.controller;

import com.arrayindex.productmanagementapi.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Product Controller Validation Tests")
class ProductControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 400 when creating product with null name")
    void createProduct_WithNullName_ShouldReturn400() throws Exception {
        Product product = new Product();
        product.setDescription("Test Description");
        product.setPrice(99.99);
        // Name is null

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @DisplayName("Should return 400 when creating product with empty name")
    void createProduct_WithEmptyName_ShouldReturn400() throws Exception {
        Product product = new Product();
        product.setName("");
        product.setDescription("Test Description");
        product.setPrice(99.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @DisplayName("Should return 400 when creating product with null description")
    void createProduct_WithNullDescription_ShouldReturn400() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(99.99);
        // Description is null

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.description").exists());
    }

    @Test
    @DisplayName("Should return 400 when creating product with null price")
    void createProduct_WithNullPrice_ShouldReturn400() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        // Price is null

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    @DisplayName("Should return 400 when creating product with negative price")
    void createProduct_WithNegativePrice_ShouldReturn400() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(-10.0);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    @DisplayName("Should return 400 when creating product with zero price")
    void createProduct_WithZeroPrice_ShouldReturn400() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(0.0);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    @DisplayName("Should return 400 when creating product with excessively long name")
    void createProduct_WithExcessivelyLongName_ShouldReturn400() throws Exception {
        Product product = new Product();
        product.setName("A".repeat(300)); // 300 characters
        product.setDescription("Test Description");
        product.setPrice(99.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @DisplayName("Should return 400 when creating product with excessively long description")
    void createProduct_WithExcessivelyLongDescription_ShouldReturn400() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("A".repeat(2000)); // 2000 characters
        product.setPrice(99.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.description").exists());
    }

    @Test
    @DisplayName("Should return 400 when updating product with invalid data")
    void updateProduct_WithInvalidData_ShouldReturn400() throws Exception {
        Product product = new Product();
        product.setName(""); // Empty name
        product.setDescription("Test Description");
        product.setPrice(99.99);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @DisplayName("Should handle malformed JSON gracefully")
    void createProduct_WithMalformedJson_ShouldReturn400() throws Exception {
        String malformedJson = "{\"name\": \"Test Product\", \"description\": \"Test Description\", \"price\": }";

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing content type")
    void createProduct_WithoutContentType_ShouldReturn415() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);

        mockMvc.perform(post("/api/products")
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Should handle oversized request body")
    void createProduct_WithOversizedRequest_ShouldReturn413() throws Exception {
        // Create a very large request body
        StringBuilder largeDescription = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeDescription.append("Very large description text. ");
        }

        Product product = new Product();
        product.setName("Test Product");
        product.setDescription(largeDescription.toString());
        product.setPrice(99.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isPayloadTooLarge());
    }

    @Test
    @DisplayName("Should handle special characters in input")
    void createProduct_WithSpecialCharacters_ShouldHandleGracefully() throws Exception {
        Product product = new Product();
        product.setName("Test <Product> & "more"");
        product.setDescription("Description with <script>alert('xss')</script>");
        product.setPrice(99.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should handle SQL injection attempts")
    void createProduct_WithSQLInjection_ShouldHandleSafely() throws Exception {
        Product product = new Product();
        product.setName("'; DROP TABLE products; --");
        product.setDescription("Normal description");
        product.setPrice(99.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());
    }
}