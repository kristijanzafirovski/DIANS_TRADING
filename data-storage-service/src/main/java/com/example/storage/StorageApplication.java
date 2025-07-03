package com.example.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class StorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(StorageApplication.class, args);
    }

    @Bean
    public ApplicationRunner init(CqlSession cassandra, JdbcTemplate jdbcTemplate) {
        return args -> {
            // 1) Create keyspace if it doesn't exist
            cassandra.execute("""
                CREATE KEYSPACE IF NOT EXISTS trading
                WITH replication = {'class':'SimpleStrategy','replication_factor':1};
                """);

            // 2) Switch into that keyspace
            cassandra.execute("USE trading;");

            // 3) Create the Cassandra table
            cassandra.execute("""
                CREATE TABLE IF NOT EXISTS data (
                  symbol text,
                  timestamp bigint,
                  price double,
                  PRIMARY KEY(symbol, timestamp)
                );
                """);

            // 4) Create the same table in Postgres as a fallback
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS data (
                  symbol VARCHAR(100),
                  price DOUBLE PRECISION,
                  timestamp BIGINT
                );
                """);
        };
    }
}
