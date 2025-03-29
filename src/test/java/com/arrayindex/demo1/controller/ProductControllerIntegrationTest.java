package com.arrayindex.demo1.controller;

import com.arrayindex.demo1.model.Product;
import com.arrayindex.demo1.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(99.99);
        testProduct = productRepository.save(testProduct);
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(testProduct.getName()))
                .andExpect(jsonPath("$[0].description").value(testProduct.getDescription()))
                .andExpect(jsonPath("$[0].price").value(testProduct.getPrice()));
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        mockMvc.perform(get("/api/products/{id}", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testProduct.getName()))
                .andExpect(jsonPath("$.description").value(testProduct.getDescription()))
                .andExpect(jsonPath("$.price").value(testProduct.getPrice()));
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setPrice(149.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newProduct.getName()))
                .andExpect(jsonPath("$.description").value(newProduct.getDescription()))
                .andExpect(jsonPath("$.price").value(newProduct.getPrice()));
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateAndReturnProduct() throws Exception {
        testProduct.setName("Updated Product");
        testProduct.setDescription("Updated Description");
        testProduct.setPrice(199.99);

        mockMvc.perform(put("/api/products/{id}", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testProduct.getName()))
                .andExpect(jsonPath("$.description").value(testProduct.getDescription()))
                .andExpect(jsonPath("$.price").value(testProduct.getPrice()));
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldReturn404() throws Exception {
        testProduct.setId(999L);
        testProduct.setName("Updated Product");

        mockMvc.perform(put("/api/products/{id}", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", testProduct.getId()))
                .andExpect(status().isOk());
    }
} 