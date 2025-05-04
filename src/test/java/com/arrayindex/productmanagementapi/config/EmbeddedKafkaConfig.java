package com.arrayindex.productmanagementapi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.test.context.EmbeddedKafka;

@TestConfiguration
@Profile("test")
@EmbeddedKafka(partitions = 1, topics = {"products"})
public class EmbeddedKafkaConfig {

    @Bean
    @Primary
    public KafkaAdmin.NewTopics topics() {
        return new KafkaAdmin.NewTopics(
            TopicBuilder.name("products")
                .partitions(1)
                .replicas(1)
                .build()
        );
    }
}
