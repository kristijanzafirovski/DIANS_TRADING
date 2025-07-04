package com.example.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication(
        exclude = DataSourceAutoConfiguration.class
)
@EnableKafka
@EnableCassandraRepositories(basePackages = "com.example.storage.repository")
public class StorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(StorageApplication.class, args);
    }
}
