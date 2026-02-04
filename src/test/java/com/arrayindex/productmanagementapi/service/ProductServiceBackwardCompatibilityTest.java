package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.dto.ProductDTO;
import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceBackwardCompatibilityTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PriceHistoryRepository priceHistoryRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ProductService productService;

    @Test
    void testCreateProductWithMinimalFields() {
        // Test creating product with only required fields (backward compatibility)
        ProductDTO minimalDTO = new ProductDTO();
        minimalDTO.setName("Test Product");
        minimalDTO.setPrice(99.99);
        minimalDTO.setDescription("Test description");
        // Note: category, stockQuantity, sku, isActive are null

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Test Product");
        savedProduct.setPrice(99.99);
        savedProduct.setDescription("Test description");
        savedProduct.setCategory("Uncategorized"); // Default value
        savedProduct.setStockQuantity(0); // Default value
        savedProduct.setSku("SKU-12345"); // Auto-generated
        savedProduct.setIsActive(true); // Default value

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productRepository.findBySku(anyString())).thenReturn(java.util.Optional.empty());

        Product result = productService.createProduct(minimalDTO);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals("Uncategorized", result.getCategory()); // Should get default
        assertEquals(0, result.getStockQuantity()); // Should get default
        assertEquals(true, result.getIsActive()); // Should get default
        assertNotNull(result.getSku()); // Should be auto-generated

        verify(productRepository).save(any(Product.class));
        verify(kafkaProducerService).sendProduct(any(Product.class));
    }

    @Test
    void testUpdateProductWithPartialData() {
        // Test updating product with partial data (backward compatibility)
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Existing Product");
        existingProduct.setPrice(50.0);
        existingProduct.setCategory("Electronics");
        existingProduct.setStockQuantity(10);
        existingProduct.setSku("SKU-EXISTING");
        existingProduct.setIsActive(true);

        ProductDTO partialUpdateDTO = new ProductDTO();
        partialUpdateDTO.setName("Updated Product");
        partialUpdateDTO.setPrice(75.0);
        // Note: category, stockQuantity, sku, isActive are null - should preserve existing values

        when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);
        when(productRepository.findBySku(anyString())).thenReturn(java.util.Optional.empty());

        Product result = productService.updateProduct(productId, partialUpdateDTO);

        assertNotNull(result);
        assertEquals("Updated Product", result.getName()); // Should be updated
        assertEquals(75.0, result.getPrice()); // Should be updated
        assertEquals("Electronics", result.getCategory()); // Should preserve existing
        assertEquals(10, result.getStockQuantity()); // Should preserve existing
        assertEquals("SKU-EXISTING", result.getSku()); // Should preserve existing
        assertEquals(true, result.getIsActive()); // Should preserve existing

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }
}