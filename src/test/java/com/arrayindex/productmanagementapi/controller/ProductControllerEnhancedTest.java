package com.arrayindex.productmanagementapi.controller;

import com.arrayindex.productmanagementapi.dto.ProductDTO;
import com.arrayindex.productmanagementapi.dto.ProductSearchDTO;
import com.arrayindex.productmanagementapi.exception.ProductNotFoundException;
import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerEnhancedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;
    private Product product;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setDescription("Test Description");
        productDTO.setPrice(99.99);
        productDTO.setCategory("Electronics");
        productDTO.setStockQuantity(10);
        productDTO.setSku("SKU-TEST123");
        productDTO.setIsActive(true);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);
        product.setCategory("Electronics");
        product.setStockQuantity(10);
        product.setSku("SKU-TEST123");
        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createProduct_ValidInput_ReturnsCreated() throws Exception {
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).createProduct(any(ProductDTO.class));
    }

    @Test
    void createProduct_InvalidInput_ReturnsBadRequest() throws Exception {
        ProductDTO invalidDTO = new ProductDTO();
        invalidDTO.setName(""); // Invalid name

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchProducts_ValidCriteria_ReturnsProducts() throws Exception {
        ProductSearchDTO searchDTO = new ProductSearchDTO();
        searchDTO.setName("Test");
        searchDTO.setPage(0);
        searchDTO.setSize(10);

        List<Product> products = Arrays.asList(product);
        Page<Product> productPage = new PageImpl<>(products);

        when(productService.searchProducts(any(ProductSearchDTO.class))).thenReturn(productPage);

        mockMvc.perform(post("/api/products/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
    }

    @Test
    void getProductsByCategory_ValidCategory_ReturnsProducts() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getProductsByCategory("Electronics")).thenReturn(products);

        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Electronics"));
    }

    @Test
    void updateStock_ValidInput_ReturnsUpdatedProduct() throws Exception {
        product.setStockQuantity(15);
        when(productService.updateStock(eq(1L), eq(5), eq("INCREASE"))).thenReturn(product);

        mockMvc.perform(put("/api/products/1/stock")
                .param("quantity", "5")
                .param("operation", "INCREASE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(15));
    }

    @Test
    void getProductPriceHistory_ValidId_ReturnsHistory() throws Exception {
        List<PriceHistory> priceHistory = Arrays.asList();
        when(productService.getProductPriceHistory(1L)).thenReturn(priceHistory);

        mockMvc.perform(get("/api/products/1/price-history"))
                .andExpect(status().isOk());
    }

    @Test
    void bulkDeleteProducts_ValidInput_ReturnsSuccess() throws Exception {
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(delete("/api/products/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productIds)))
                .andExpect(status().isOk());
    }

    @Test
    void getProductStatistics_ReturnsStatistics() throws Exception {
        mockMvc.perform(get("/api/products/statistics"))
                .andExpect(status().isOk());
    }
}