package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.repository.ProductRepository;
import com.arrayindex.productmanagementapi.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

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

        assertThat(products).hasSize(1);
        assertThat(products.get(0)).isEqualTo(testProduct);
        verify(productRepository).findAll();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        var product = productService.getProductById(1L);

        assertThat(product).isPresent();
        assertThat(product.get()).isEqualTo(testProduct);
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturnEmpty() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        var product = productService.getProductById(1L);

        assertThat(product).isEmpty();
        verify(productRepository).findById(1L);
    }

    @Test
    void createProduct_ShouldReturnSavedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        doNothing().when(kafkaProducerService).sendProduct(any(Product.class));

        var savedProduct = productService.createProduct(testProduct);

        assertThat(savedProduct).isEqualTo(testProduct);
        verify(productRepository).save(testProduct);
        verify(kafkaProducerService).sendProduct(testProduct);
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateAndReturnProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        var updatedProduct = productService.updateProduct(1L, testProduct);

        assertThat(updatedProduct).isEqualTo(testProduct);
        verify(productRepository).existsById(1L);
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldThrowException() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> productService.updateProduct(1L, testProduct))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Product not found with id: 1");

        verify(productRepository).existsById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_WhenProductDoesNotExist_ShouldThrowException() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteProduct(1L))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessage("Product not found with id: 1");

        verify(productRepository).existsById(1L);
        verify(productRepository, never()).deleteById(any());
    }
} 