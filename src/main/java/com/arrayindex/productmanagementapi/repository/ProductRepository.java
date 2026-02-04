package com.arrayindex.productmanagementapi.repository;

import com.arrayindex.productmanagementapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Custom query methods for enhanced functionality
    
    /**
     * Find products by name containing keyword (case-insensitive)
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find products by price range
     */
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    
    /**
     * Find products by name and price range
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByNameAndPriceRange(@Param("name") String name, @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    /**
     * Find products with price greater than specified value
     */
    List<Product> findByPriceGreaterThan(Double price);
    
    /**
     * Find products with price less than specified value
     */
    List<Product> findByPriceLessThan(Double price);
    
    /**
     * Count products by price range
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Long countByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    /**
     * Find products ordered by price descending
     */
    List<Product> findAllByOrderByPriceDesc();
    
    /**
     * Find products ordered by price ascending
     */
    List<Product> findAllByOrderByPriceAsc();
    
    /**
     * Find products ordered by name
     */
    List<Product> findAllByOrderByNameAsc();
    
    /**
     * Find products by exact name (case-insensitive)
     */
    Optional<Product> findByNameIgnoreCase(String name);
    
    /**
     * Check if product exists by name (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Count total products
     */
    @Query("SELECT COUNT(p) FROM Product p")
    long countTotalProducts();
    
    /**
     * Find products with description containing keyword
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findByDescriptionContaining(@Param("keyword") String keyword);
    
    /**
     * Find products by price greater than or equal to specified value
     */
    List<Product> findByPriceGreaterThanEqual(Double price);
    
    /**
     * Find products by price less than or equal to specified value
     */
    List<Product> findByPriceLessThanEqual(Double price);
}