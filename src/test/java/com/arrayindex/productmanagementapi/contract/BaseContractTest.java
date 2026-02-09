package com.arrayindex.productmanagementapi.contract;

import com.arrayindex.productmanagementapi.model.Product;
import com.arrayindex.productmanagementapi.repository.ProductRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Autowired(required = false)
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        // Required for Spring Cloud Contract generated tests (RestAssuredMockMvc).
        RestAssuredMockMvc.mockMvc(mockMvc);

        // Keep contracts stable by ensuring a known product exists at /api/products/1.
        // We reset the table where possible so H2 identity restarts at 1.
        if (jdbcTemplate != null) {
            try {
                jdbcTemplate.execute("TRUNCATE TABLE products RESTART IDENTITY");
            } catch (DataAccessException ignored) {
                // Fallback for dialects that don't support RESTART IDENTITY
                try {
                    jdbcTemplate.execute("TRUNCATE TABLE products");
                } catch (DataAccessException ignored2) {
                    productRepository.deleteAll();
                }
            }
        } else {
            productRepository.deleteAll();
        }

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
