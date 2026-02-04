package com.arrayindex.productmanagementapi.controller;

import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/buggy")
public class BuggyController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private DataSource dataSource;

    // BUG 1: SQL Injection vulnerability
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String query) {
        List<Product> results = new ArrayList<>();
        try {
            Connection conn = dataSource.getConnection();
            // VULNERABLE: Direct string concatenation in SQL
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM products WHERE name LIKE '%" + query + "%'";
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getDouble("price"));
                results.add(product);
            }
            
            // BUG 2: Resource leak - connections not closed properly
            // Missing rs.close(), stmt.close(), conn.close()
            
        } catch (Exception e) {
            // BUG 3: Poor exception handling - swallowing exceptions
            System.out.println("Error occurred: " + e.getMessage());
        }
        return results;
    }

    // BUG 4: No input validation
    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // BUG 5: No null checks
        product.setName(product.getName().toUpperCase());
        
        // BUG 6: Potential N+1 query problem
        List<Product> allProducts = productService.getAllProducts();
        for (Product p : allProducts) {
            // Simulate checking each product individually (N+1 pattern)
            productService.getProductById(p.getId());
        }
        
        Product saved = productService.createProduct(product);
        return ResponseEntity.ok(saved);
    }

    // BUG 7: Sensitive information exposure
    @GetMapping("/admin/debug")
    public ResponseEntity<String> debugInfo(@RequestParam String userId) {
        // BUG 8: No authentication/authorization check
        String debugInfo = "Database URL: jdbc:h2:mem:productdb\n" +
                          "Username: sa\n" +
                          "Password: password123\n" +
                          "User requesting: " + userId;
        
        return ResponseEntity.ok(debugInfo);
    }

    // BUG 9: Inefficient loop and potential memory issues
    @GetMapping("/heavy-operation")
    public ResponseEntity<String> heavyOperation() {
        List<String> hugeLst = new ArrayList<>();
        
        // BUG 10: Potential memory leak - creating huge list
        for (int i = 0; i < 1000000; i++) {
            hugeLst.add("Item " + i + " with some additional text to consume memory");
        }
        
        // BUG 11: Inefficient string concatenation
        String result = "";
        for (String item : hugeLst) {
            result += item + ", ";
        }
        
        return ResponseEntity.ok("Processed " + hugeLst.size() + " items");
    }

    // BUG 12: Race condition potential
    private static int counter = 0;
    
    @GetMapping("/counter")
    public ResponseEntity<Integer> incrementCounter() {
        // BUG 13: Thread safety issue - no synchronization
        counter++;
        
        // BUG 14: Using System.out instead of proper logging
        System.out.println("Counter incremented to: " + counter);
        
        return ResponseEntity.ok(counter);
    }

    // BUG 15: Improper exception handling with generic Exception
    @GetMapping("/risky-operation")
    public ResponseEntity<String> riskyOperation() throws Exception {
        try {
            // Simulate some risky operation
            if (Math.random() > 0.5) {
                throw new RuntimeException("Random failure");
            }
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            // BUG 16: Re-throwing generic Exception
            throw new Exception("Operation failed", e);
        }
    }
}
