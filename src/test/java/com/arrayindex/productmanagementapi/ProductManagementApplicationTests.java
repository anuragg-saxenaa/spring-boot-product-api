package com.arrayindex.productmanagementapi;

import com.arrayindex.productmanagementapi.config.EmbeddedKafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Import(EmbeddedKafkaConfig.class)
@EmbeddedKafka(partitions = 1, topics = {"products"})
class ProductManagementApplicationTests {

    @Test
    void contextLoads() {
        log.info("Context loads successfully with embedded Kafka");
    }
}