package com.arrayindex.productmanagementapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProductManagementApplicationTests {

    @Test
    void contextLoads() {
        // Basic smoke test: verifies Spring context starts.
        System.out.println("✅ Context loads");
    }
}
