package com.arrayindex.productmanagementapi.contract;

import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base class for Spring Cloud Contract generated tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseContractTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ProductRepository productRepository;

    @BeforeEach
    void seed() {
        // Ensure at least one product exists for GET contracts.
        if (productRepository.count() == 0) {
            Product p = new Product();
            p.setName("Contract Product");
            p.setDescription("Seed data for contract tests");
            p.setPrice(99.99);
            p.setCategory("Contract");
            p.setStockQuantity(10);
            p.setIsActive(true);
            productRepository.save(p);
        }
    }
}
