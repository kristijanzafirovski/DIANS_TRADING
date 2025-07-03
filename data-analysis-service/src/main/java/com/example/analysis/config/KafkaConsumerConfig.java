package com.example.analysis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    // (We rely on Spring Boot's auto-configuration via application.properties)
}
