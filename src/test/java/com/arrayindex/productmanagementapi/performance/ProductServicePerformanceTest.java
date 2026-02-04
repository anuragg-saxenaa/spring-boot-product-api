package com.arrayindex.productmanagementapi.performance;

import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Product Service Performance Tests")
class ProductServicePerformanceTest {

    @Autowired
    private ProductService productService;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        productService.getAllProducts().forEach(product -> 
            productService.deleteProduct(product.getId())
        );
    }

    @Test
    @DisplayName("Should create 100 products within 5 seconds")
    void createProducts_Batch100_ShouldCompleteWithin5Seconds() {
        List<Product> products = createTestProducts(100);

        assertTimeoutPreemptively(java.time.Duration.ofSeconds(5), () -> {
            products.forEach(productService::createProduct);
        });

        List<Product> savedProducts = productService.getAllProducts();
        assertThat(savedProducts).hasSize(100);
    }

    @Test
    @DisplayName("Should retrieve all products within 1 second")
    void getAllProducts_With100Products_ShouldCompleteWithin1Second() {
        // Setup: Create 100 products
        List<Product> products = createTestProducts(100);
        products.forEach(productService::createProduct);

        // Test: Retrieve all products
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(1), () -> {
            List<Product> retrievedProducts = productService.getAllProducts();
            assertThat(retrievedProducts).hasSize(100);
        });
    }

    @Test
    @DisplayName("Should retrieve product by ID within 500ms")
    void getProductById_ShouldCompleteWithin500ms() {
        // Setup: Create a product
        Product product = createTestProduct("Performance Test Product", "Description", 99.99);
        Product savedProduct = productService.createProduct(product);

        // Test: Retrieve by ID
        assertTimeoutPreemptively(java.time.Duration.ofMillis(500), () -> {
            var retrievedProduct = productService.getProductById(savedProduct.getId());
            assertThat(retrievedProduct).isPresent();
            assertThat(retrievedProduct.get().getName()).isEqualTo("Performance Test Product");
        });
    }

    @Test
    @DisplayName("Should handle concurrent product creation")
    void createProducts_Concurrent_ShouldHandleCorrectly() throws Exception {
        List<Product> products = createTestProducts(50);

        // Create products concurrently
        List<CompletableFuture<Product>> futures = products.stream()
                .map(product -> CompletableFuture.supplyAsync(() -> productService.createProduct(product)))
                .toList();

        // Wait for all to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(10, TimeUnit.SECONDS);

        // Verify all products were created
        List<Product> savedProducts = productService.getAllProducts();
        assertThat(savedProducts).hasSize(50);
    }

    @Test
    @DisplayName("Should update product within 500ms")
    void updateProduct_ShouldCompleteWithin500ms() {
        // Setup: Create a product
        Product product = createTestProduct("Original Product", "Original Description", 99.99);
        Product savedProduct = productService.createProduct(product);

        // Update product
        savedProduct.setName("Updated Product");
        savedProduct.setPrice(149.99);

        // Test: Update within time limit
        assertTimeoutPreemptively(java.time.Duration.ofMillis(500), () -> {
            Product updatedProduct = productService.updateProduct(savedProduct.getId(), savedProduct);
            assertThat(updatedProduct.getName()).isEqualTo("Updated Product");
            assertThat(updatedProduct.getPrice()).isEqualTo(149.99);
        });
    }

    @Test
    @DisplayName("Should delete product within 500ms")
    void deleteProduct_ShouldCompleteWithin500ms() {
        // Setup: Create a product
        Product product = createTestProduct("Product to Delete", "Description", 99.99);
        Product savedProduct = productService.createProduct(product);

        // Test: Delete within time limit
        assertTimeoutPreemptively(java.time.Duration.ofMillis(500), () -> {
            productService.deleteProduct(savedProduct.getId());
            
            var deletedProduct = productService.getProductById(savedProduct.getId());
            assertThat(deletedProduct).isEmpty();
        });
    }

    @Test
    @DisplayName("Should handle stress test with 1000 products")
    void stressTest_With1000Products_ShouldHandleGracefully() {
        // Setup: Create 1000 products
        List<Product> products = createTestProducts(1000);
        
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(30), () -> {
            products.forEach(productService::createProduct);
        });

        // Verify all products were created
        List<Product> savedProducts = productService.getAllProducts();
        assertThat(savedProducts).hasSize(1000);

        // Verify performance doesn't degrade
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(5), () -> {
            List<Product> retrievedProducts = productService.getAllProducts();
            assertThat(retrievedProducts).hasSize(1000);
        });
    }

    @Test
    @DisplayName("Should search products by name efficiently")
    void searchProducts_ByName_ShouldBeEfficient() {
        // Setup: Create products with similar names
        IntStream.range(0, 100).forEach(i -> {
            Product product = createTestProduct("Product " + i, "Description " + i, 99.99 + i);
            productService.createProduct(product);
        });

        // Test: Search by name pattern
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () -> {
            List<Product> foundProducts = productService.getAllProducts().stream()
                    .filter(p -> p.getName().contains("Product 5"))
                    .toList();
            
            assertThat(foundProducts).hasSize(11); // Product 5, 50-59
        });
    }

    // Helper methods
    private Product createTestProduct(String name, String description, Double price) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        return product;
    }

    private List<Product> createTestProducts(int count) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Product product = createTestProduct(
                    "Test Product " + i,
                    "Test Description " + i,
                    99.99 + (i * 0.01)
            );
            products.add(product);
        }
        return products;
    }
}