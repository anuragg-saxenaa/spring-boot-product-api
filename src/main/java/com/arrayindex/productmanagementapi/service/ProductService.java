package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.dto.ProductDTO;
import com.arrayindex.productmanagementapi.dto.ProductSearchDTO;
import com.arrayindex.productmanagementapi.exception.ProductNotFoundException;
import com.arrayindex.productmanagementapi.exception.InsufficientStockException;
import com.arrayindex.productmanagementapi.exception.DuplicateSkuException;
import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.model.PriceHistory;
import com.arrayindex.productmanagementapi.repository.ProductRepository;
import com.arrayindex.productmanagementapi.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final KafkaProducerService kafkaProducerService;

    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }

    @Cacheable(value = "productById", key = "#id")
    public Optional<Product> getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id);
    }

    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public Product createProduct(ProductDTO productDTO) {
        log.info("Creating new product: {}", productDTO.getName());
        
        // Check for duplicate SKU
        if (productDTO.getSku() != null && productRepository.findBySku(productDTO.getSku()).isPresent()) {
            throw new DuplicateSkuException("Product with SKU " + productDTO.getSku() + " already exists");
        }
        
        Product product = convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        
        // Send to Kafka for asynchronous processing
        kafkaProducerService.sendProduct(savedProduct);
        
        log.info("Product created successfully with id: {}", savedProduct.getId());
        return savedProduct;
    }

    @Transactional
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public Product updateProduct(Long id, ProductDTO productDTO) {
        log.info("Updating product with id: {}", id);
        
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        
        // Check for duplicate SKU if SKU is being changed
        if (productDTO.getSku() != null && !existingProduct.getSku().equals(productDTO.getSku())) {
            if (productRepository.findBySku(productDTO.getSku()).isPresent()) {
                throw new DuplicateSkuException("Product with SKU " + productDTO.getSku() + " already exists");
            }
        }
        
        // Track price changes
        if (productDTO.getPrice() != null && !existingProduct.getPrice().equals(productDTO.getPrice())) {
            PriceHistory priceHistory = new PriceHistory();
            priceHistory.setProduct(existingProduct);
            priceHistory.setOldPrice(existingProduct.getPrice());
            priceHistory.setNewPrice(productDTO.getPrice());
            priceHistory.setChangeReason("Product update");
            priceHistoryRepository.save(priceHistory);
        }
        
        updateEntity(existingProduct, productDTO);
        Product updatedProduct = productRepository.save(existingProduct);
        
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

    public Page<Product> searchProducts(ProductSearchDTO searchDTO) {
        log.info("Searching products with criteria: {}", searchDTO);
        
        Sort.Direction direction = searchDTO.getSortDirection().equalsIgnoreCase("DESC") 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, searchDTO.getSortBy());
        Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), sort);
        
        return productRepository.findBySearchCriteria(
                searchDTO.getName(),
                searchDTO.getCategory(),
                searchDTO.getMinPrice(),
                searchDTO.getMaxPrice(),
                searchDTO.getIsActive(),
                pageable
        );
    }

    @Cacheable(value = "productsByCategory", key = "#category")
    public List<Product> getProductsByCategory(String category) {
        log.info("Fetching products by category: {}", category);
        return productRepository.findByCategory(category);
    }

    @Cacheable(value = "activeProducts")
    public List<Product> getActiveProducts() {
        log.info("Fetching active products");
        return productRepository.findByIsActiveTrue();
    }

    @Cacheable(value = "productsByName", key = "#name")
    public List<Product> searchProductsByName(String name) {
        log.info("Searching products by name: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Cacheable(value = "productsByDescription", key = "#description")
    public List<Product> searchProductsByDescription(String description) {
        log.info("Searching products by description: {}", description);
        return productRepository.findByDescriptionContainingIgnoreCase(description);
    }

    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        log.info("Fetching products by price range: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Product> getLowStockProducts(Integer threshold) {
        log.info("Fetching products with stock below threshold: {}", threshold);
        return productRepository.findLowStockProducts(threshold);
    }

    public List<Object[]> getProductsCountByCategory() {
        log.info("Fetching product count by category");
        return productRepository.countProductsByCategory();
    }

    public List<Product> getRecentlyAddedProducts(int limit) {
        log.info("Fetching recently added products, limit: {}", limit);
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.findRecentlyAddedProducts(pageable);
    }

    @Transactional
    public Product updateStock(Long id, Integer quantity, String operation) {
        log.info("Updating stock for product {}: {} {}", id, operation, quantity);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        
        if ("INCREASE".equalsIgnoreCase(operation)) {
            product.setStockQuantity(product.getStockQuantity() + quantity);
        } else if ("DECREASE".equalsIgnoreCase(operation)) {
            if (product.getStockQuantity() < quantity) {
                throw new InsufficientStockException("Insufficient stock. Available: " + product.getStockQuantity() + ", Requested: " + quantity);
            }
            product.setStockQuantity(product.getStockQuantity() - quantity);
        } else {
            throw new IllegalArgumentException("Invalid operation. Use 'INCREASE' or 'DECREASE'");
        }
        
        Product updatedProduct = productRepository.save(product);
        kafkaProducerService.sendProduct(updatedProduct);
        
        log.info("Stock updated successfully for product {}. New stock: {}", id, updatedProduct.getStockQuantity());
        return updatedProduct;
    }

    public List<PriceHistory> getProductPriceHistory(Long productId) {
        log.info("Fetching price history for product: {}", productId);
        return priceHistoryRepository.findByProductIdOrderByChangedAtDesc(productId);
    }

    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        
        // Handle backward compatibility - provide defaults for new fields
        product.setCategory(dto.getCategory() != null ? dto.getCategory() : "Uncategorized");
        product.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);
        product.setSku(dto.getSku()); // Will be auto-generated if null
        product.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return product;
    }

    private void updateEntity(Product product, ProductDTO dto) {
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getCategory() != null) product.setCategory(dto.getCategory());
        if (dto.getStockQuantity() != null) product.setStockQuantity(dto.getStockQuantity());
        if (dto.getSku() != null) product.setSku(dto.getSku());
        if (dto.getIsActive() != null) product.setIsActive(dto.getIsActive());
    }
}