package com.arrayindex.demo1.service;

import com.arrayindex.demo1.model.Product;
import com.arrayindex.demo1.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(99.99);
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));

        var products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals(testProduct.getName(), products.get(0).getName());
        verify(productRepository).findAll();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        var result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals(testProduct.getName(), result.get().getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturnEmpty() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        var result = productService.getProductById(1L);

        assertFalse(result.isPresent());
        verify(productRepository).findById(1L);
    }

    @Test
    void createProduct_ShouldReturnSavedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        var result = productService.createProduct(testProduct);

        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateAndReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        var result = productService.updateProduct(1L, testProduct);

        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldReturnNull() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        var result = productService.updateProduct(1L, testProduct);

        assertNull(result);
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_ShouldCallRepositoryDelete() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }
} 