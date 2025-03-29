package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@DirtiesContext
class KafkaIntegrationTest {

    private static final KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.1")
    );

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        log.info("Starting Kafka container...");
        kafka.start();
        String bootstrapServers = kafka.getBootstrapServers();
        log.info("Kafka bootstrap servers: {}", bootstrapServers);
        registry.add("spring.kafka.bootstrap-servers", () -> bootstrapServers);
        registry.add("spring.kafka.consumer.group-id", () -> "product-group");
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.consumer.key-deserializer", () -> "org.apache.kafka.common.serialization.StringDeserializer");
        registry.add("spring.kafka.consumer.value-deserializer", () -> "org.springframework.kafka.support.serializer.JsonDeserializer");
        registry.add("spring.kafka.consumer.properties.spring.json.trusted.packages", () -> "com.arrayindex.productmanagementapi.model");
        registry.add("spring.kafka.producer.key-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("spring.kafka.producer.value-serializer", () -> "org.springframework.kafka.support.serializer.JsonSerializer");
        registry.add("spring.kafka.properties.allow.auto.create.topics", () -> "true");
    }

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

    @AfterAll
    static void tearDown() {
        log.info("Cleaning up Kafka container...");
        if (kafka != null) {
            try {
                kafka.stop();
                kafka.close();
                log.info("Kafka container stopped and closed successfully");
            } catch (Exception e) {
                log.error("Error while cleaning up Kafka container", e);
            }
        }
    }
} 