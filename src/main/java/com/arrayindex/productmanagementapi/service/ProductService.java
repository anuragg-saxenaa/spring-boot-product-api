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
import org.springframework.beans.factory.annotation.Autowired;
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
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final KafkaProducerService kafkaProducerService;
    private final AIModelService aiModelService;

    @Autowired
    public ProductService(ProductRepository productRepository, 
                         PriceHistoryRepository priceHistoryRepository,
                         KafkaProducerService kafkaProducerService,
                         AIModelService aiModelService) {
        this.productRepository = productRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.aiModelService = aiModelService;
    }

    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        System.out.println("Fetching all products");
        return productRepository.findAll();
    }

    @Cacheable(value = "productById", key = "#id")
    public Optional<Product> getProductById(Long id) {
        System.out.println("Fetching product with id: " + id);
        return productRepository.findById(id);
    }

    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public Product createProduct(ProductDTO productDTO) {
        return createProduct(productDTO, null); // Use default AI model
    }

    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public Product createProduct(ProductDTO productDTO, String aiModel) {
        System.out.println("Creating new product: " + productDTO.getName());
        
        // Check for duplicate SKU
        if (productDTO.getSku() != null && productRepository.findBySku(productDTO.getSku()).isPresent()) {
            throw new DuplicateSkuException("Product with SKU " + productDTO.getSku() + " already exists");
        }
        
        // AI-powered description generation if description is empty and AI model is specified
        if ((productDTO.getDescription() == null || productDTO.getDescription().trim().isEmpty()) && aiModel != null) {
            try {
                String generatedDescription = aiModelService.generateProductDescription(
                    productDTO.getName(), 
                    productDTO.getCategory() != null ? productDTO.getCategory() : "General",
                    aiModel
                );
                productDTO.setDescription(generatedDescription);
                System.out.println("AI-generated description for product: " + productDTO.getName());
            } catch (Exception e) {
                System.out.println("Failed to generate AI description: " + e.getMessage());
                // Continue without AI description
            }
        }
        
        Product product = convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        
        // Send to Kafka for asynchronous processing
        kafkaProducerService.sendProduct(savedProduct);
        
        System.out.println("Product created successfully with id: " + savedProduct.getId());
        return savedProduct;
    }

    @Transactional
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public Product updateProduct(Long id, ProductDTO productDTO) {
        System.out.println("Updating product with id: " + id);
        
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
        
        System.out.println("Product updated successfully with id: " + id);
        return updatedProduct;
    }

    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public void deleteProduct(Long id) {
        System.out.println("Deleting product with id: " + id);
        
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        
        productRepository.deleteById(id);
        System.out.println("Product deleted successfully with id: " + id);
    }

    public Page<Product> searchProducts(ProductSearchDTO searchDTO) {
        System.out.println("Searching products with criteria: " + searchDTO);
        
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
        System.out.println("Fetching products by category: " + category);
        return productRepository.findByCategory(category);
    }

    @Cacheable(value = "activeProducts")
    public List<Product> getActiveProducts() {
        System.out.println("Fetching active products");
        return productRepository.findByIsActiveTrue();
    }

    @Cacheable(value = "productsByName", key = "#name")
    public List<Product> searchProductsByName(String name) {
        System.out.println("Searching products by name: " + name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Cacheable(value = "productsByDescription", key = "#description")
    public List<Product> searchProductsByDescription(String description) {
        System.out.println("Searching products by description: " + description);
        return productRepository.findByDescriptionContainingIgnoreCase(description);
    }

    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        System.out.println("Fetching products by price range: " + minPrice + " - " + maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Product> getLowStockProducts(Integer threshold) {
        System.out.println("Fetching products with stock below threshold: " + threshold);
        return productRepository.findLowStockProducts(threshold);
    }

    public List<Object[]> getProductsCountByCategory() {
        System.out.println("Fetching product count by category");
        return productRepository.countProductsByCategory();
    }

    public List<Product> getRecentlyAddedProducts(int limit) {
        System.out.println("Fetching recently added products, limit: " + limit);
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.findRecentlyAddedProducts(pageable);
    }

    @Transactional
    public Product updateStock(Long id, Integer quantity, String operation) {
        System.out.println("Updating stock for product " + id + ": " + operation + " " + quantity);
        
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
        
        System.out.println("Stock updated successfully for product " + id + ". New stock: " + updatedProduct.getStockQuantity());
        return updatedProduct;
    }

    public List<PriceHistory> getProductPriceHistory(Long productId) {
        System.out.println("Fetching price history for product: " + productId);
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