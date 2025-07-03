package com.example.ingestion;


import com.example.storage.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class StartupDataLoader implements ApplicationRunner {
    @Autowired
    private RestTemplate rest;

    private final TradeRepository repo;


    public StartupDataLoader(TradeRepository repo) {
        this.repo = repo;
        this.rest = new RestTemplate();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> symbols = repo.findDistinctSymbols();
        int required = Math.max(5, 14);
        for (String symbol : symbols) {
            long count = repo.countBySymbol(symbol);
            if (count < required) {
                rest.getForObject(
            "http://data-ingestion-service:3000/ingest/" + symbol,
                Void.class
                );
            }
        }
    }
}