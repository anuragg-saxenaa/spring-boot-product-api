package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.KafkaException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Product> kafkaTemplate;
    
    @Value("${spring.kafka.enabled:true}")
    private boolean kafkaEnabled;

    public void sendProduct(Product product) {
        if (!kafkaEnabled) {
            log.info("Kafka is disabled, skipping product event publishing");
            return;
        }
        
        try {
            log.info("Sending product to Kafka: {}", product);
            String key = product.getId() != null ? product.getId().toString() : "new-product";
            kafkaTemplate.send("products", key, product)
                .addCallback(
                    result -> log.info("Successfully sent product {} to Kafka", product.getId()),
                    failure -> log.error("Failed to send product {} to Kafka: {}", product.getId(), failure.getMessage())
                );
        } catch (KafkaException e) {
            log.error("Kafka exception while sending product {}: {}", product.getId(), e.getMessage());
            // Don't throw - continue operation without Kafka
        } catch (Exception e) {
            log.error("Unexpected error while sending product {} to Kafka: {}", product.getId(), e.getMessage());
            // Don't throw - continue operation without Kafka
        }
    }
} 