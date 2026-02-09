package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.ProductManagementApplication;
import com.arrayindex.productmanagementapi.config.EmbeddedKafkaConfig;
import com.arrayindex.productmanagementapi.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = ProductManagementApplication.class)
@ActiveProfiles("test")
@Import(EmbeddedKafkaConfig.class)
@EmbeddedKafka(partitions = 1, topics = {"products"})
class KafkaIntegrationTest {

    @Autowired
    private KafkaProducerService producerService;

    @Autowired
    private KafkaConsumerService consumerService;

    @Test
    void testKafkaIntegration() throws InterruptedException {
        log.info("Starting Kafka integration test...");

        // Create a test product
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);

        // Send the product to Kafka
        log.info("Sending product to Kafka: {}", product);
        producerService.sendProduct(product);

        // Wait for the consumer to process the message
        log.info("Waiting for consumer to process message...");
        TimeUnit.SECONDS.sleep(2);

        // Verify that the message was processed (you can add more assertions based on your consumer logic)
        // For now, we're just verifying that the test completes without errors
        log.info("Test completed successfully");
    }


}