package com.example.storage.controller;

import com.example.storage.model.Trade;
import com.example.storage.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin(origins = "*")
public class TradeController {

    private static final Logger log = LoggerFactory.getLogger(TradeController.class);
    private final TradeRepository repository;
    private final String ingestionUrl = "http://data-ingestion-service:3000";

    /** track which tickers are currently being ingested */
    private final Set<String> pendingIngestions = ConcurrentHashMap.newKeySet();
    @Autowired
    private RestTemplate restTemplate;

    public TradeController(TradeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/data")
    public List<Trade> getAllTrades() {
        log.info("Get all trades");
        return repository.findAll();
    }

    @GetMapping("/symbols/{ticker}")
    public ResponseEntity<List<Trade>> getTickerTrade(@PathVariable("ticker") String ticker) {
        if (repository.existsByKeySymbol(ticker)) {
            log.info("Returning cached trades for {}", ticker);
            pendingIngestions.remove(ticker);
            return ResponseEntity.ok(repository.findByKeySymbol(ticker));
        }

        if (pendingIngestions.add(ticker)) {
            log.info("No data for {}, triggering ingestion", ticker);
            String uri = String.format("%s/ingest/%s", ingestionUrl, ticker);

            CompletableFuture.runAsync(() -> {
                try {
                    restTemplate.getForEntity(uri, Void.class);
                    log.info("Ingestion request for {}", ticker);
                } catch (Exception e) {
                    log.error("Failed ingestion call for {}: {}", ticker, e.getMessage(), e);
                    pendingIngestions.remove(ticker);
                }
            });
        }

        // 3) Immediately respond “Accepted” so front-end can show a loading state
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Collections.emptyList());
    }
}
