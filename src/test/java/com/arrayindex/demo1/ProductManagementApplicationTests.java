package com.arrayindex.productmanagementapi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@SpringBootTest
class ProductManagementApplicationTests {

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

    @Test
    void contextLoads() {
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