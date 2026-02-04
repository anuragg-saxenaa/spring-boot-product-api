package com.arrayindex.productmanagementapi.controller;

import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Controller", description = "Enhanced APIs for managing products with advanced search and analytics")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    // Basic CRUD operations (existing)
    @Operation(summary = "Get all products", description = "Retrieves a list of all available products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "ID of the product to retrieve") @PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new product", description = "Creates a new product in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created product")
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @Parameter(description = "Product details to create") @RequestBody Product product) {
        return ResponseEntity.status(201).body(productService.createProduct(product));
    }

    @Operation(summary = "Update product", description = "Updates an existing product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated product"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "ID of the product to update") @PathVariable Long id,
            @Parameter(description = "Updated product details") @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return updatedProduct != null ? ResponseEntity.ok(updatedProduct) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete product", description = "Deletes a product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted product")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID of the product to delete") @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    // Enhanced search and filtering endpoints
    @Operation(summary = "Search products by name", description = "Search products by name (case-insensitive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping("/search/name")
    public ResponseEntity<List<Product>> searchProductsByName(
            @Parameter(description = "Product name to search") @RequestParam String name) {
        log.info("Searching products by name: {}", name);
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    @Operation(summary = "Get products by price range", description = "Retrieves products within a specific price range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @Parameter(description = "Minimum price") @RequestParam Double minPrice,
            @Parameter(description = "Maximum price") @RequestParam Double maxPrice) {
        log.info("Fetching products by price range: {} - {}", minPrice, maxPrice);
        return ResponseEntity.ok(productService.getProductsByPriceRange(minPrice, maxPrice));
    }

    @Operation(summary = "Get products by name and price range", description = "Search products by name within a price range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping("/search/advanced")
    public ResponseEntity<List<Product>> searchProductsAdvanced(
            @Parameter(description = "Product name to search") @RequestParam String name,
            @Parameter(description = "Minimum price") @RequestParam Double minPrice,
            @Parameter(description = "Maximum price") @RequestParam Double maxPrice) {
        log.info("Advanced search - name: {}, price range: {} - {}", name, minPrice, maxPrice);
        return ResponseEntity.ok(productService.findByNameAndPriceRange(name, minPrice, maxPrice));
    }

    @Operation(summary = "Get products ordered by price", description = "Retrieves products ordered by price (ascending or descending)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping("/ordered-by-price")
    public ResponseEntity<List<Product>> getProductsOrderedByPrice(
            @Parameter(description = "Sort direction: ASC or DESC") @RequestParam(defaultValue = "ASC") String direction) {
        log.info("Fetching products ordered by price: {}", direction);
        return ResponseEntity.ok(productService.getProductsOrderedByPrice(direction));
    }

    @Operation(summary = "Get products ordered by name", description = "Retrieves products ordered alphabetically by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping("/ordered-by-name")
    public ResponseEntity<List<Product>> getProductsOrderedByName() {
        log.info("Fetching products ordered by name");
        return ResponseEntity.ok(productService.getProductsOrderedByName());
    }

    @Operation(summary = "Check if product exists by name", description = "Checks if a product exists with the given name (case-insensitive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully checked existence")
    })
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Map<String, Boolean>> checkProductExistsByName(
            @Parameter(description = "Product name to check") @PathVariable String name) {
        log.info("Checking if product exists by name: {}", name);
        boolean exists = productService.existsByNameIgnoreCase(name);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Count products by price range", description = "Counts the number of products within a specific price range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    })
    @GetMapping("/count-by-price-range")
    public ResponseEntity<Map<String, Long>> countProductsByPriceRange(
            @Parameter(description = "Minimum price") @RequestParam Double minPrice,
            @Parameter(description = "Maximum price") @RequestParam Double maxPrice) {
        log.info("Counting products by price range: {} - {}", minPrice, maxPrice);
        long count = productService.countProductsByPriceRange(minPrice, maxPrice);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get total product count", description = "Retrieves the total number of products in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    })
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalProductCount() {
        log.info("Fetching total product count");
        long count = productService.countTotalProducts();
        Map<String, Long> response = new HashMap<>();
        response.put("totalCount", count);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search products by description", description = "Search products by description containing keyword")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping("/search/description")
    public ResponseEntity<List<Product>> searchProductsByDescription(
            @Parameter(description = "Keyword to search in description") @RequestParam String keyword) {
        log.info("Searching products by description keyword: {}", keyword);
        return ResponseEntity.ok(productService.searchProductsByDescription(keyword));
    }

    @Operation(summary = "Get product statistics", description = "Retrieves various product statistics and metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics")
    })
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getProductStatistics() {
        log.info("Fetching product statistics");
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalProducts", productService.countTotalProducts());
        statistics.put("productsByPriceRange", getProductsByPriceRanges());
        statistics.put("priceDistribution", getPriceDistribution());
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Get products by price greater than", description = "Retrieves products with price greater than specified value")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping("/price-greater-than")
    public ResponseEntity<List<Product>> getProductsByPriceGreaterThan(
            @Parameter(description = "Minimum price threshold") @RequestParam Double price) {
        log.info("Fetching products with price greater than: {}", price);
        return ResponseEntity.ok(productService.getProductsByPriceGreaterThan(price));
    }

    @Operation(summary = "Get products by price less than", description = "Retrieves products with price less than specified value")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping("/price-less-than")
    public ResponseEntity<List<Product>> getProductsByPriceLessThan(
            @Parameter(description = "Maximum price threshold") @RequestParam Double price) {
        log.info("Fetching products with price less than: {}", price);
        return ResponseEntity.ok(productService.getProductsByPriceLessThan(price));
    }

    // Helper methods for statistics
    private Map<String, Long> getProductsByPriceRanges() {
        Map<String, Long> priceRanges = new HashMap<>();
        priceRanges.put("0-50", productService.countProductsByPriceRange(0.0, 50.0));
        priceRanges.put("50-100", productService.countProductsByPriceRange(50.0, 100.0));
        priceRanges.put("100-500", productService.countProductsByPriceRange(100.0, 500.0));
        priceRanges.put("500+", productService.countProductsByPriceRange(500.0, Double.MAX_VALUE));
        return priceRanges;
    }

    private Map<String, Double> getPriceDistribution() {
        Map<String, Double> distribution = new HashMap<>();
        List<Product> allProducts = productService.getAllProducts();
        
        if (allProducts.isEmpty()) {
            distribution.put("min", 0.0);
            distribution.put("max", 0.0);
            distribution.put("average", 0.0);
        } else {
            double minPrice = allProducts.stream().mapToDouble(Product::getPrice).min().orElse(0.0);
            double maxPrice = allProducts.stream().mapToDouble(Product::getPrice).max().orElse(0.0);
            double avgPrice = allProducts.stream().mapToDouble(Product::getPrice).average().orElse(0.0);
            
            distribution.put("min", minPrice);
            distribution.put("max", maxPrice);
            distribution.put("average", avgPrice);
        }
        
        return distribution;
    }
}