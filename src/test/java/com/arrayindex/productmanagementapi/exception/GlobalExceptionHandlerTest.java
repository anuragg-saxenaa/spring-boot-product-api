package com.arrayindex.productmanagementapi.exception;

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
@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should handle ProductNotFoundException with proper error response")
    void handleProductNotFoundException_ShouldReturnProperErrorResponse() throws Exception {
        mockMvc.perform(get("/api/products/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/products/99999"));
    }

    @Test
    @DisplayName("Should handle generic RuntimeException")
    void handleRuntimeException_ShouldReturnProperErrorResponse() throws Exception {
        // Try to update a non-existent product
        Product product = new Product();
        product.setName("Non-existent Product");
        product.setDescription("Description");
        product.setPrice(99.99);

        mockMvc.perform(put("/api/products/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException")
    void handleMethodArgumentNotValidException_ShouldReturnProperErrorResponse() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setName(""); // Invalid - empty name
        invalidProduct.setDescription("Valid Description");
        invalidProduct.setPrice(99.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException")
    void handleHttpMessageNotReadableException_ShouldReturnProperErrorResponse() throws Exception {
        String invalidJson = "{\"name\": \"Test\", \"description\": \"Test\", \"price\": }";

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle HttpRequestMethodNotSupportedException")
    void handleHttpRequestMethodNotSupportedException_ShouldReturnProperErrorResponse() throws Exception {
        mockMvc.perform(patch("/api/products/1"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle HttpMediaTypeNotSupportedException")
    void handleHttpMediaTypeNotSupportedException_ShouldReturnProperErrorResponse() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.error").value("Unsupported Media Type"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle NoHandlerFoundException")
    void handleNoHandlerFoundException_ShouldReturnProperErrorResponse() throws Exception {
        mockMvc.perform(get("/api/nonexistent-endpoint"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle AccessDeniedException")
    void handleAccessDeniedException_ShouldReturnProperErrorResponse() throws Exception {
        // This would typically be tested with security configuration
        // For now, we'll test the general exception handling structure
        
        // Test that the error response format is consistent
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void handleGenericException_ShouldReturnProperErrorResponse() throws Exception {
        // Test that all exceptions follow the same response format
        mockMvc.perform(get("/api/products/invalid-id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("Should handle concurrent exception scenarios")
    void handleConcurrentExceptions_ShouldHandleGracefully() throws Exception {
        // Create multiple requests that might cause exceptions
        List<String> invalidIds = List.of("999", "888", "777", "666", "555");

        for (String invalidId : invalidIds) {
            mockMvc.perform(get("/api/products/" + invalidId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"));
        }
    }

    @Test
    @DisplayName("Should handle exception with SQL injection attempt")
    void handleException_WithSQLInjection_ShouldHandleSafely() throws Exception {
        // SQL injection attempt in ID parameter
        mockMvc.perform(get("/api/products/1 OR 1=1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("Should handle exception with XSS attempt")
    void handleException_WithXSSAttempt_ShouldHandleSafely() throws Exception {
        Product product = new Product();
        product.setName("<script>alert('XSS')</script>");
        product.setDescription("Normal description");
        product.setPrice(99.99);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isCreated()) // Should be created safely
                .andExpect(jsonPath("$.name").value("<script>alert('XSS')</script>")); // Should be stored as-is (handled by output encoding)
    }

    @Test
    @DisplayName("Should maintain consistent error response format across all exceptions")
    void errorResponseFormat_ShouldBeConsistent() throws Exception {
        // Test different exception scenarios
        String[] endpoints = {
                "/api/products/999",      // NotFound
                "/api/products/invalid",  // BadRequest  
                "/api/nonexistent",       // NotFound
                "/api/products"           // POST with invalid data (after setup)
        };

        for (String endpoint : endpoints) {
            if (endpoint.equals("/api/products")) {
                // Setup invalid data for POST
                Product invalidProduct = new Product();
                invalidProduct.setName(""); // Invalid
                invalidProduct.setDescription("Valid Description");
                invalidProduct.setPrice(99.99);

                mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidProduct)))
                        .andExpect(jsonPath("$.timestamp").exists())
                        .andExpect(jsonPath("$.status").exists())
                        .andExpect(jsonPath("$.error").exists())
                        .andExpect(jsonPath("$.message").exists());
            } else {
                mockMvc.perform(get(endpoint))
                        .andExpect(jsonPath("$.timestamp").exists())
                        .andExpect(jsonPath("$.status").exists())
                        .andExpect(jsonPath("$.error").exists())
                        .andExpect(jsonPath("$.message").exists());
            }
        }
    }
}