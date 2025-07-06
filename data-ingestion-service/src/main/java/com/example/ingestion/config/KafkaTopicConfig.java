package com.example.ingestion.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaTopicConfig {

    /**
     * This bean will configure the Admin client.
     * Spring Boot auto-configures it if you have spring-kafka on the classpath
     * and have set spring.kafka.bootstrap-servers.
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka:9092");
        return new KafkaAdmin(configs);
    }

    /**
     * Define a topic called "trades", with 3 partitions and replication factor 1.
     * On startup, KafkaAdmin will check and create it if missing.
     */
    @Bean
    public NewTopic tradesTopic() {
        return new NewTopic("trades", 3, (short) 1);
    }
}
