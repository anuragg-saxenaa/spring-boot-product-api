package com.arrayindex.productmanagementapi.controller;

import com.arrayindex.productmanagementapi.ProductManagementApplication;
import com.arrayindex.productmanagementapi.config.EmbeddedKafkaConfig;
import com.arrayindex.productmanagementapi.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest(classes = ProductManagementApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(EmbeddedKafkaConfig.class)
@EmbeddedKafka(partitions = 1, topics = {"products"})
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        // First create a product
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);

        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Product createdProduct = objectMapper.readValue(response, Product.class);

        // Then get it by ID
        mockMvc.perform(get("/api/products/" + createdProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdProduct.getId()))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateAndReturnProduct() throws Exception {
        // First create a product
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);

        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Product createdProduct = objectMapper.readValue(response, Product.class);

        // Update the product
        createdProduct.setName("Updated Product");
        createdProduct.setPrice(149.99);

        mockMvc.perform(put("/api/products/" + createdProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdProduct.getId()))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(149.99));
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldReturn404() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);

        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_ShouldReturn200() throws Exception {
        // First create a product
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);

        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Product createdProduct = objectMapper.readValue(response, Product.class);

        // Then delete it
        mockMvc.perform(delete("/api/products/" + createdProduct.getId()))
                .andExpect(status().isOk());
    }


}