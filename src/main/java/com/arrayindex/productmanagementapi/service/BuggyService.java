package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class BuggyService {

    // BUG 1: Field injection instead of constructor injection
    @Autowired
    private ProductRepository productRepository;
    
    // BUG 2: Static field in Spring component
    private static ProductRepository staticRepo;

    // BUG 3: Improper @PostConstruct usage
    @PostConstruct
    public void init() {
        staticRepo = productRepository; // Anti-pattern: setting static field
        System.out.println("BuggyService initialized"); // BUG: System.out instead of logger
    }

    // BUG 4: Missing @Transactional on method that should be transactional
    public Product saveWithoutTransaction(Product product) {
        // This should be transactional but isn't
        Product saved = productRepository.save(product);
        
        // Simulate additional operations that should be in same transaction
        if (saved.getPrice() > 1000) {
            // This could fail and leave data in inconsistent state
            updateExpensiveProductFlag(saved.getId());
        }
        
        return saved;
    }

    // BUG 5: Overly broad @Transactional
    @Transactional
    public List<Product> getAllProductsWithUnnecessaryTransaction() {
        // Read-only operation doesn't need transaction
        return productRepository.findAll();
    }

    // BUG 6: Long-running operation in transaction
    @Transactional
    public void longRunningOperation() {
        List<Product> products = productRepository.findAll();
        
        for (Product product : products) {
            // BUG 7: Expensive operation inside transaction
            try {
                Thread.sleep(1000); // Simulating long operation
            } catch (InterruptedException e) {
                // BUG 8: Improper exception handling
                e.printStackTrace();
            }
            
            // BUG 9: N+1 query problem
            productRepository.save(product);
        }
    }

    // BUG 10: Method violates single responsibility principle
    @Transactional
    public String processProductAndGenerateReport(Product product) {
        // Multiple responsibilities in one method
        
        // 1. Validate product
        if (product.getName() == null) {
            throw new RuntimeException("Invalid product"); // BUG: Generic exception
        }
        
        // 2. Save product
        Product saved = productRepository.save(product);
        
        // 3. Generate report (should be separate service)
        StringBuilder report = new StringBuilder();
        report.append("Product Report\n");
        report.append("ID: ").append(saved.getId()).append("\n");
        report.append("Name: ").append(saved.getName()).append("\n");
        
        // BUG 11: File I/O in transaction
        try {
            Thread.sleep(500); // Simulating file write
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 4. Send notification (should be async)
        sendNotification(saved);
        
        return report.toString();
    }

    // BUG 12: Synchronous operation that should be async
    private void sendNotification(Product product) {
        System.out.println("Sending notification for product: " + product.getName());
        // Simulate API call delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // BUG 13: Using static method with repository
    public static Product getProductStatic(Long id) {
        // Anti-pattern: static method accessing instance field
        return staticRepo.findById(id).orElse(null);
    }

    // BUG 14: Method too complex (high cyclomatic complexity)
    public String complexMethod(Product product, String operation) {
        if (product == null) {
            if (operation.equals("create")) {
                return "Cannot create null product";
            } else if (operation.equals("update")) {
                return "Cannot update null product";
            } else if (operation.equals("delete")) {
                return "Cannot delete null product";
            } else {
                return "Unknown operation";
            }
        } else {
            if (product.getName() == null) {
                if (operation.equals("create")) {
                    return "Cannot create product without name";
                } else {
                    return "Product name is required";
                }
            } else if (product.getName().length() < 3) {
                if (operation.equals("create")) {
                    return "Product name too short for creation";
                } else {
                    return "Product name too short";
                }
            } else {
                if (operation.equals("create")) {
                    Product saved = productRepository.save(product);
                    return "Created: " + saved.getName();
                } else if (operation.equals("update")) {
                    Product saved = productRepository.save(product);
                    return "Updated: " + saved.getName();
                } else {
                    return "Operation completed";
                }
            }
        }
    }

    private void updateExpensiveProductFlag(Long productId) {
        // Simulate additional database operation
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            // This would normally update some flag
            productRepository.save(product);
        }
    }
}
