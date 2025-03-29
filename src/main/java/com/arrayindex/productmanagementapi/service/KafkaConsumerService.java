package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ProductService productService;

    @KafkaListener(topics = "products", groupId = "product-group")
    public void consumeProduct(Product product) {
        log.info("Received product from Kafka: {}", product);
        // Here you can add any additional processing logic
        // For example, sending notifications, updating other systems, etc.
    }
} 