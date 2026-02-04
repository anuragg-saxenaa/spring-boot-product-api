package com.arrayindex.productmanagementapi.repository;

import com.arrayindex.productmanagementapi.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Product Repository Tests")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct1;
    private Product testProduct2;
    private Product testProduct3;

    @BeforeEach
    void setUp() {
        // Clear existing data
        productRepository.deleteAll();
        entityManager.flush();

        // Create test data
        testProduct1 = new Product();
        testProduct1.setName("iPhone 15");
        testProduct1.setDescription("Latest Apple smartphone with advanced features");
        testProduct1.setPrice(999.99);

        testProduct2 = new Product();
        testProduct2.setName("Samsung Galaxy S24");
        testProduct2.setDescription("Premium Android smartphone with AI capabilities");
        testProduct2.setPrice(899.99);

        testProduct3 = new Product();
        testProduct3.setName("MacBook Pro M3");
        testProduct3.setDescription("High-performance laptop for professionals");
        testProduct3.setPrice(2499.99);

        // Save test data
        entityManager.persist(testProduct1);
        entityManager.persist(testProduct2);
        entityManager.persist(testProduct3);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find products by name containing keyword (case-insensitive)")
    void findByNameContainingIgnoreCase_ShouldReturnMatchingProducts() {
        // When
        List<Product> products = productRepository.findByNameContainingIgnoreCase("phone");

        // Then
        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getName)
                .containsExactlyInAnyOrder("iPhone 15", "Samsung Galaxy S24");
    }

    @Test
    @DisplayName("Should return empty list when no products match name containing keyword")
    void findByNameContainingIgnoreCase_WhenNoMatch_ShouldReturnEmptyList() {
        // When
        List<Product> products = productRepository.findByNameContainingIgnoreCase("tablet");

        // Then
        assertThat(products).isEmpty();
    }

    @Test
    @DisplayName("Should find products by price range")
    void findByPriceBetween_ShouldReturnProductsInPriceRange() {
        // When
        List<Product> products = productRepository.findByPriceBetween(800.0, 1200.0);

        // Then
        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getPrice)
                .containsExactlyInAnyOrder(999.99, 899.99);
    }

    @Test
    @DisplayName("Should return empty list when no products in price range")
    void findByPriceBetween_WhenNoProductsInRange_ShouldReturnEmptyList() {
        // When
        List<Product> products = productRepository.findByPriceBetween(100.0, 500.0);

        // Then
        assertThat(products).isEmpty();
    }

    @Test
    @DisplayName("Should find products by name and price range")
    void findByNameAndPriceRange_ShouldReturnMatchingProducts() {
        // When
        List<Product> products = productRepository.findByNameAndPriceRange("galaxy", 800.0, 1000.0);

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Samsung Galaxy S24");
        assertThat(products.get(0).getPrice()).isEqualTo(899.99);
    }

    @Test
    @DisplayName("Should count products by price range")
    void countByPriceRange_ShouldReturnCorrectCount() {
        // When
        Long count = productRepository.countByPriceRange(800.0, 1500.0);

        // Then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should return 0 when no products in price range")
    void countByPriceRange_WhenNoProductsInRange_ShouldReturnZero() {
        // When
        Long count = productRepository.countByPriceRange(100.0, 500.0);

        // Then
        assertThat(count).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should find products ordered by price descending")
    void findAllByOrderByPriceDesc_ShouldReturnProductsOrderedByPriceDesc() {
        // When
        List<Product> products = productRepository.findAllByOrderByPriceDesc();

        // Then
        assertThat(products).hasSize(3);
        assertThat(products).extracting(Product::getPrice)
                .containsExactly(2499.99, 999.99, 899.99);
    }

    @Test
    @DisplayName("Should find products ordered by price ascending")
    void findAllByOrderByPriceAsc_ShouldReturnProductsOrderedByPriceAsc() {
        // When
        List<Product> products = productRepository.findAllByOrderByPriceAsc();

        // Then
        assertThat(products).hasSize(3);
        assertThat(products).extracting(Product::getPrice)
                .containsExactly(899.99, 999.99, 2499.99);
    }

    @Test
    @DisplayName("Should find products ordered by name ascending")
    void findAllByOrderByNameAsc_ShouldReturnProductsOrderedByName() {
        // When
        List<Product> products = productRepository.findAllByOrderByNameAsc();

        // Then
        assertThat(products).hasSize(3);
        assertThat(products).extracting(Product::getName)
                .containsExactly("MacBook Pro M3", "Samsung Galaxy S24", "iPhone 15");
    }

    @Test
    @DisplayName("Should find product by exact name (case-insensitive)")
    void findByNameIgnoreCase_ShouldReturnMatchingProduct() {
        // When
        Optional<Product> product = productRepository.findByNameIgnoreCase("iphone 15");

        // Then
        assertThat(product).isPresent();
        assertThat(product.get().getName()).isEqualTo("iPhone 15");
    }

    @Test
    @DisplayName("Should return empty when product name not found (case-insensitive)")
    void findByNameIgnoreCase_WhenNotFound_ShouldReturnEmpty() {
        // When
        Optional<Product> product = productRepository.findByNameIgnoreCase("pixel 8");

        // Then
        assertThat(product).isEmpty();
    }

    @Test
    @DisplayName("Should check if product exists by name (case-insensitive)")
    void existsByNameIgnoreCase_ShouldReturnTrueWhenExists() {
        // When
        boolean exists = productRepository.existsByNameIgnoreCase("iphone 15");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when product does not exist by name (case-insensitive)")
    void existsByNameIgnoreCase_ShouldReturnFalseWhenNotExists() {
        // When
        boolean exists = productRepository.existsByNameIgnoreCase("pixel 8");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should count total products")
    void countTotalProducts_ShouldReturnCorrectCount() {
        // When
        long count = productRepository.countTotalProducts();

        // Then
        assertThat(count).isEqualTo(3L);
    }

    @Test
    @DisplayName("Should find products with description containing keyword")
    void findByDescriptionContaining_ShouldReturnMatchingProducts() {
        // When
        List<Product> products = productRepository.findByDescriptionContaining("smartphone");

        // Then
        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getName)
                .containsExactlyInAnyOrder("iPhone 15", "Samsung Galaxy S24");
    }

    @Test
    @DisplayName("Should find products by price greater than or equal")
    void findByPriceGreaterThanEqual_ShouldReturnProductsWithPriceGTE() {
        // When
        List<Product> products = productRepository.findByPriceGreaterThanEqual(1000.0);

        // Then
        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getPrice)
                .containsExactlyInAnyOrder(999.99, 2499.99);
    }

    @Test
    @DisplayName("Should find products by price less than or equal")
    void findByPriceLessThanEqual_ShouldReturnProductsWithPriceLTE() {
        // When
        List<Product> products = productRepository.findByPriceLessThanEqual(1000.0);

        // Then
        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getPrice)
                .containsExactlyInAnyOrder(999.99, 899.99);
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void findByNameContainingIgnoreCase_WithNull_ShouldReturnEmptyList() {
        // When
        List<Product> products = productRepository.findByNameContainingIgnoreCase(null);

        // Then
        assertThat(products).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty strings gracefully")
    void findByNameContainingIgnoreCase_WithEmptyString_ShouldReturnAllProducts() {
        // When
        List<Product> products = productRepository.findByNameContainingIgnoreCase("");

        // Then
        assertThat(products).hasSize(3);
    }
}