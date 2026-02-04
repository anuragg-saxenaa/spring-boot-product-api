package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.repository.ProductRepository;
import com.arrayindex.productmanagementapi.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaProducerService kafkaProducerService;

    // Basic CRUD operations
    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll();
    }

    @Cacheable(value = "productById", key = "#id")
    public Optional<Product> getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        return productRepository.findById(id);
    }

    @Transactional
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());
        Product savedProduct = productRepository.save(product);
        kafkaProducerService.sendProduct(savedProduct);
        log.info("Product created successfully with id: {}", savedProduct.getId());
        return savedProduct;
    }

    @Transactional
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public Product updateProduct(Long id, Product product) {
        log.info("Updating product with id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        product.setId(id);
        Product updatedProduct = productRepository.save(product);
        kafkaProducerService.sendProduct(updatedProduct);
        log.info("Product updated successfully with id: {}", id);
        return updatedProduct;
    }

    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted successfully with id: {}", id);
    }

    // Enhanced search and filtering methods
    @Cacheable(value = "productsByName", key = "#name")
    public List<Product> searchProductsByName(String name) {
        log.debug("Searching products by name: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Cacheable(value = "productsByPriceRange", key = "#minPrice + '-' + #maxPrice")
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        log.debug("Fetching products by price range: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Product> findByNameAndPriceRange(String name, Double minPrice, Double maxPrice) {
        log.debug("Advanced search - name: {}, price range: {} - {}", name, minPrice, maxPrice);
        return productRepository.findByNameAndPriceRange(name, minPrice, maxPrice);
    }

    public List<Product> getProductsOrderedByPrice(String direction) {
        log.debug("Fetching products ordered by price: {}", direction);
        if ("DESC".equalsIgnoreCase(direction)) {
            return productRepository.findAllByOrderByPriceDesc();
        } else {
            return productRepository.findAllByOrderByPriceAsc();
        }
    }

    public List<Product> getProductsOrderedByName() {
        log.debug("Fetching products ordered by name");
        return productRepository.findAllByOrderByNameAsc();
    }

    public boolean existsByNameIgnoreCase(String name) {
        log.debug("Checking if product exists by name: {}", name);
        return productRepository.existsByNameIgnoreCase(name);
    }

    public long countProductsByPriceRange(Double minPrice, Double maxPrice) {
        log.debug("Counting products by price range: {} - {}", minPrice, maxPrice);
        return productRepository.countByPriceRange(minPrice, maxPrice);
    }

    public long countTotalProducts() {
        log.debug("Counting total products");
        return productRepository.countTotalProducts();
    }

    public List<Product> searchProductsByDescription(String keyword) {
        log.debug("Searching products by description keyword: {}", keyword);
        return productRepository.findByDescriptionContaining(keyword);
    }

    public List<Product> getProductsByPriceGreaterThan(Double price) {
        log.debug("Fetching products with price greater than: {}", price);
        return productRepository.findByPriceGreaterThan(price);
    }

    public List<Product> getProductsByPriceLessThan(Double price) {
        log.debug("Fetching products with price less than: {}", price);
        return productRepository.findByPriceLessThan(price);
    }

    public List<Product> getProductsByPriceGreaterThanEqual(Double price) {
        log.debug("Fetching products with price greater than or equal to: {}", price);
        return productRepository.findByPriceGreaterThanEqual(price);
    }

    public List<Product> getProductsByPriceLessThanEqual(Double price) {
        log.debug("Fetching products with price less than or equal to: {}", price);
        return productRepository.findByPriceLessThanEqual(price);
    }

    // Advanced analytics and statistics
    public Map<String, Object> getProductStatistics() {
        log.info("Generating product statistics");
        Map<String, Object> statistics = new HashMap<>();
        
        // Total count
        statistics.put("totalProducts", countTotalProducts());
        
        // Price range distribution
        Map<String, Long> priceRanges = new HashMap<>();
        priceRanges.put("0-50", countProductsByPriceRange(0.0, 50.0));
        priceRanges.put("50-100", countProductsByPriceRange(50.0, 100.0));
        priceRanges.put("100-500", countProductsByPriceRange(100.0, 500.0));
        priceRanges.put("500+", countProductsByPriceRange(500.0, Double.MAX_VALUE));
        statistics.put("priceRangeDistribution", priceRanges);
        
        // Price statistics
        List<Product> allProducts = getAllProducts();
        if (!allProducts.isEmpty()) {
            double minPrice = allProducts.stream().mapToDouble(Product::getPrice).min().orElse(0.0);
            double maxPrice = allProducts.stream().mapToDouble(Product::getPrice).max().orElse(0.0);
            double avgPrice = allProducts.stream().mapToDouble(Product::getPrice).average().orElse(0.0);
            
            Map<String, Double> priceStats = new HashMap<>();
            priceStats.put("min", minPrice);
            priceStats.put("max", maxPrice);
            priceStats.put("average", avgPrice);
            statistics.put("priceStatistics", priceStats);
        }
        
        return statistics;
    }

    // Helper method for bulk operations
    @Transactional
    public Map<String, Integer> bulkDeleteProducts(List<Long> productIds) {
        log.info("Performing bulk delete for {} products", productIds.size());
        int deletedCount = 0;
        int notFoundCount = 0;
        
        for (Long id : productIds) {
            try {
                if (productRepository.existsById(id)) {
                    productRepository.deleteById(id);
                    deletedCount++;
                } else {
                    notFoundCount++;
                }
            } catch (Exception e) {
                log.error("Error deleting product with id: {}", id, e);
                notFoundCount++;
            }
        }
        
        Map<String, Integer> result = new HashMap<>();
        result.put("deletedCount", deletedCount);
        result.put("notFoundCount", notFoundCount);
        
        log.info("Bulk delete completed. Deleted: {}, Not found: {}", deletedCount, notFoundCount);
        return result;
    }
}