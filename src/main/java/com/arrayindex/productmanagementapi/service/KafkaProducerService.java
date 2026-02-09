package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.KafkaException;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, Product> kafkaTemplate;
    
    @Value("${spring.kafka.enabled:true}")
    private boolean kafkaEnabled;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, Product> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendProduct(Product product) {
        if (!kafkaEnabled) {
            log.info("Kafka is disabled, skipping product event publishing");
            return;
        }
        
        try {
            log.info("Sending product to Kafka: {}", product);
            String key = product.getId() != null ? product.getId().toString() : "new-product";
            
            // Use CompletableFuture with whenComplete instead of addCallback
            kafkaTemplate.send("products", key, product).whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error("Failed to send product {} to Kafka: {}", product.getId(), exception.getMessage());
                } else {
                    log.info("Successfully sent product {} to Kafka", product.getId());
                }
            });
            
        } catch (KafkaException e) {
            log.error("Kafka exception while sending product {}: {}", product.getId(), e.getMessage());
            // Don't throw - continue operation without Kafka
        } catch (Exception e) {
            log.error("Unexpected error while sending product {} to Kafka: {}", product.getId(), e.getMessage());
            // Don't throw - continue operation without Kafka
        }
    }
}