package com.arrayindex.productmanagementapi.controller;

import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerAdvancedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);
    }

    @Test
    void searchProductsByName_ValidName_ReturnsProducts() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.searchProductsByName("Test")).thenReturn(products);

        mockMvc.perform(get("/api/products/search/name")
                .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void getProductsByPriceRange_ValidRange_ReturnsProducts() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getProductsByPriceRange(50.0, 150.0)).thenReturn(products);

        mockMvc.perform(get("/api/products/price-range")
                .param("minPrice", "50.0")
                .param("maxPrice", "150.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(99.99));
    }

    @Test
    void searchProductsAdvanced_ValidParams_ReturnsProducts() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.findByNameAndPriceRange("Test", 50.0, 150.0)).thenReturn(products);

        mockMvc.perform(get("/api/products/search/advanced")
                .param("name", "Test")
                .param("minPrice", "50.0")
                .param("maxPrice", "150.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void getProductsOrderedByPrice_ValidDirection_ReturnsProducts() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getProductsOrderedByPrice("ASC")).thenReturn(products);

        mockMvc.perform(get("/api/products/ordered-by-price")
                .param("direction", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void checkProductExistsByName_ValidName_ReturnsExists() throws Exception {
        when(productService.existsByNameIgnoreCase("Test Product")).thenReturn(true);

        mockMvc.perform(get("/api/products/exists/name/Test Product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }

    @Test
    void countProductsByPriceRange_ValidRange_ReturnsCount() throws Exception {
        when(productService.countProductsByPriceRange(50.0, 150.0)).thenReturn(5L);

        mockMvc.perform(get("/api/products/count-by-price-range")
                .param("minPrice", "50.0")
                .param("maxPrice", "150.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    void getTotalProductCount_ReturnsCount() throws Exception {
        when(productService.countTotalProducts()).thenReturn(10L);

        mockMvc.perform(get("/api/products/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(10));
    }

    @Test
    void searchProductsByDescription_ValidKeyword_ReturnsProducts() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.searchProductsByDescription("Test")).thenReturn(products);

        mockMvc.perform(get("/api/products/search/description")
                .param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Test Description"));
    }

    @Test
    void getProductStatistics_ReturnsStatistics() throws Exception {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalProducts", 10L);
        
        Map<String, Long> priceRanges = new HashMap<>();
        priceRanges.put("0-50", 3L);
        priceRanges.put("50-100", 5L);
        priceRanges.put("100-500", 2L);
        statistics.put("priceRangeDistribution", priceRanges);
        
        Map<String, Double> priceStats = new HashMap<>();
        priceStats.put("min", 25.0);
        priceStats.put("max", 200.0);
        priceStats.put("average", 75.0);
        statistics.put("priceStatistics", priceStats);
        
        when(productService.getProductStatistics()).thenReturn(statistics);

        mockMvc.perform(get("/api/products/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(10))
                .andExpect(jsonPath("$.priceRangeDistribution.0-50").value(3))
                .andExpect(jsonPath("$.priceStatistics.min").value(25.0));
    }

    @Test
    void getProductsByPriceGreaterThan_ValidPrice_ReturnsProducts() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getProductsByPriceGreaterThan(50.0)).thenReturn(products);

        mockMvc.perform(get("/api/products/price-greater-than")
                .param("price", "50.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(99.99));
    }

    @Test
    void bulkDeleteProducts_ValidIds_ReturnsResult() throws Exception {
        Map<String, Integer> result = new HashMap<>();
        result.put("deletedCount", 2);
        result.put("notFoundCount", 1);
        
        when(productService.bulkDeleteProducts(Arrays.asList(1L, 2L, 3L))).thenReturn(result);

        mockMvc.perform(delete("/api/products/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1, 2, 3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deletedCount").value(2))
                .andExpect(jsonPath("$.notFoundCount").value(1));
    }
}