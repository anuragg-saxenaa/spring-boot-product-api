package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ProductService productService;

    @Autowired
    public KafkaConsumerService(ProductService productService) {
        this.productService = productService;
    }

    @KafkaListener(topics = "products", groupId = "product-group")
    public void consumeProduct(Product product) {
        log.info("Received product from Kafka: {}", product);
        // Here you can add any additional processing logic
        // For example, sending notifications, updating other systems, etc.
    }
}