package com.arrayindex.productmanagementapi.controller;

import com.arrayindex.productmanagementapi.dto.ProductDTO;
import com.arrayindex.productmanagementapi.dto.ProductSearchDTO;
import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.model.PriceHistory;
import com.arrayindex.productmanagementapi.service.ProductService;
import com.arrayindex.productmanagementapi.exception.ProductNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Controller", description = "Enhanced APIs for managing products with advanced features")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all products", description = "Retrieves a list of all available products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
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
        @ApiResponse(responseCode = "201", description = "Successfully created product"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "SKU already exists")
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @Valid @RequestBody ProductDTO productDTO) {
        Product createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.status(201).body(createdProduct);
    }

    @Operation(summary = "Create AI-enhanced product", description = "Creates a new product with AI-generated description if none provided")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created product with AI enhancement"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "SKU already exists")
    })
    @PostMapping("/ai-enhanced")
    public ResponseEntity<Product> createAIEnhancedProduct(
            @Valid @RequestBody ProductDTO productDTO,
            @Parameter(description = "AI model to use (ollama, gemini, moonshot)") @RequestParam(required = false) String aiModel) {
        Product createdProduct = productService.createProduct(productDTO, aiModel);
        return ResponseEntity.status(201).body(createdProduct);
    }

    @Operation(summary = "Update product", description = "Updates an existing product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated product"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "409", description = "SKU already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        Product updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Delete product", description = "Deletes a product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted product"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Search products", description = "Search products with advanced filtering and pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @PostMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(@Valid @RequestBody ProductSearchDTO searchDTO) {
        return ResponseEntity.ok(productService.searchProducts(searchDTO));
    }

    @Operation(summary = "Get products by category", description = "Retrieves products filtered by category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @Parameter(description = "Product category") @PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @Operation(summary = "Get active products", description = "Retrieves only active products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active products")
    })
    @GetMapping("/active")
    public ResponseEntity<List<Product>> getActiveProducts() {
        return ResponseEntity.ok(productService.getActiveProducts());
    }

    @Operation(summary = "Search products by name", description = "Search products by name (case-insensitive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping("/search/name")
    public ResponseEntity<List<Product>> searchProductsByName(
            @Parameter(description = "Product name to search") @RequestParam String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    @GetMapping("/search/description")
    @Operation(summary = "Search products by description", description = "Search products by description (case-insensitive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    public ResponseEntity<List<Product>> searchProductsByDescription(
            @Parameter(description = "Description keyword to search") @RequestParam String description) {
        return ResponseEntity.ok(productService.searchProductsByDescription(description));
    }

    @Operation(summary = "Get products by price range", description = "Retrieves products within a specific price range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @Parameter(description = "Minimum price") @RequestParam Double minPrice,
            @Parameter(description = "Maximum price") @RequestParam Double maxPrice) {
        return ResponseEntity.ok(productService.getProductsByPriceRange(minPrice, maxPrice));
    }

    @Operation(summary = "Get low stock products", description = "Retrieves products with stock below specified threshold")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved low stock products")
    })
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(
            @Parameter(description = "Stock threshold") @RequestParam(defaultValue = "10") Integer threshold) {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold));
    }

    @Operation(summary = "Get product count by category", description = "Retrieves product count grouped by category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved category counts")
    })
    @GetMapping("/category-counts")
    public ResponseEntity<List<Object[]>> getProductsCountByCategory() {
        return ResponseEntity.ok(productService.getProductsCountByCategory());
    }

    @Operation(summary = "Get recently added products", description = "Retrieves recently added products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved recent products")
    })
    @GetMapping("/recent")
    public ResponseEntity<List<Product>> getRecentlyAddedProducts(
            @Parameter(description = "Number of recent products to retrieve") @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productService.getRecentlyAddedProducts(limit));
    }

    @Operation(summary = "Update product stock", description = "Increase or decrease product stock quantity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated stock"),
        @ApiResponse(responseCode = "400", description = "Invalid operation or insufficient stock"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @Parameter(description = "Quantity to adjust") @RequestParam Integer quantity,
            @Parameter(description = "Operation type: INCREASE or DECREASE") @RequestParam String operation) {
        Product updatedProduct = productService.updateStock(id, quantity, operation);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Get product price history", description = "Retrieves price change history for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved price history"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}/price-history")
    public ResponseEntity<List<PriceHistory>> getProductPriceHistory(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductPriceHistory(id));
    }

    @Operation(summary = "Bulk delete products", description = "Delete multiple products by their IDs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted products"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @DeleteMapping("/bulk")
    public ResponseEntity<Map<String, Object>> bulkDeleteProducts(@RequestBody List<Long> productIds) {
        int deletedCount = 0;
        int notFoundCount = 0;
        
        for (Long id : productIds) {
            try {
                productService.deleteProduct(id);
                deletedCount++;
            } catch (ProductNotFoundException e) {
                notFoundCount++;
            }
        }
        
        Map<String, Object> response = Map.of(
                "message", "Bulk delete completed",
                "deletedCount", deletedCount,
                "notFoundCount", notFoundCount,
                "totalRequested", productIds.size()
        );
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get product statistics", description = "Retrieves various product statistics and metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics")
    })
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getProductStatistics() {
        Map<String, Object> statistics = Map.of(
                "totalProducts", productService.getAllProducts().size(),
                "activeProducts", productService.getActiveProducts().size(),
                "lowStockProducts", productService.getLowStockProducts(10).size(),
                "categoryCounts", productService.getProductsCountByCategory(),
                "recentProducts", productService.getRecentlyAddedProducts(5)
        );
        
        return ResponseEntity.ok(statistics);
    }
}