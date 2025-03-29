package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Product> kafkaTemplate;

    public void sendProduct(Product product) {
        log.info("Sending product to Kafka: {}", product);
        String key = product.getId() != null ? product.getId().toString() : "new-product";
        kafkaTemplate.send("products", key, product);
    }
} 