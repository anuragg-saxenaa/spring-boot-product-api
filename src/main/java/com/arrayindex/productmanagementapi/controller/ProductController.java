package com.arrayindex.productmanagementapi.controller;

import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Controller", description = "APIs for managing products")
public class ProductController {

    @Autowired
    private ProductService productService;

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
        // Potential security issue: no input validation
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
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
        // TODO: Add input validation - this should trigger a review comment
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new RuntimeException("Product name cannot be null or empty");
        }
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

    // New test endpoint with intentional issues for automated review demo
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String query) {
        // Issue 1: No null validation
        // Issue 2: No SQL injection protection shown
        // Issue 3: No pagination for potentially large results
        System.out.println("Searching for: " + query); // Issue 4: Using System.out instead of logger
        return productService.getAllProducts(); // Issue 5: Not actually using the search query
    }
} 