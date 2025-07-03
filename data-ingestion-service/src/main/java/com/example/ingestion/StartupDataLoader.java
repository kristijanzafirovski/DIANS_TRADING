package com.example.ingestion;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class StartupDataLoader implements ApplicationRunner {
    @Autowired
    private RestTemplate rest;

    private final CassandraTemplate cassandra;


    public StartupDataLoader(CassandraTemplate cassandra) {
        this.cassandra = cassandra;
        this.rest = new RestTemplate();
    }

    @Override
    public void run(ApplicationArguments args) {
        // 1) grab all distinct symbols
        List<String> symbols = cassandra
                .getCqlOperations()
                .queryForList(
                        "SELECT DISTINCT symbol FROM trading.trades",
                        String.class
                );

        int required = 14; // or whatever your logic is

        for (String symbol : symbols) {
            // 2) for each symbol, count how many rows we already have
            Long count = cassandra
                    .getCqlOperations()
                    .queryForObject(
                            "SELECT count(*) FROM trading.trades WHERE symbol = ?",
                            Long.class,
                            symbol
                    );

            if (count == null || count < required) {
                // 3) kick off ingest via your own HTTP endpoint
                rest.getForObject(
                        "http://data-ingestion-service:3000/ingest/" + symbol,
                        Void.class
                );
            }
        }
    }
}