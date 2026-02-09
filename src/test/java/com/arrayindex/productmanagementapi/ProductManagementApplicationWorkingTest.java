package com.arrayindex.productmanagementapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@org.springframework.test.context.ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.kafka.enabled=false",
    "spring.redis.host=localhost"
})
class ProductManagementApplicationWorkingTest {

    @Test
    void contextLoads() {
        // Smoke test: verifies the application context loads.
        System.out.println("✅ Application context loaded successfully");
    }
}