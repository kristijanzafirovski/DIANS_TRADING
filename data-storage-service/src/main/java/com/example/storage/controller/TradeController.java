package com.example.storage.controller;

import com.example.storage.model.Trade;
import com.example.storage.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
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
    private final Set<String> pendingIngestions = ConcurrentHashMap.newKeySet();

    @Autowired
    private RestTemplate restTemplate;

    public TradeController(TradeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/symbols/{ticker}")
    public ResponseEntity<List<Trade>> getTickerTrade(@PathVariable("ticker") String ticker) {
        List<Trade> trades = repository.findByKeySymbol(ticker);
        if (trades.isEmpty()) {
            triggerIngestion(ticker);
            return ResponseEntity.accepted().body(Collections.emptyList());
        }
        Trade latest = trades.stream()
                .max(Comparator.comparingLong(Trade::getTimestamp))
                .orElseThrow();
        LocalDate tradeDate = Instant.ofEpochMilli(latest.getTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        if (!tradeDate.equals(LocalDate.now())) {
            triggerIngestion(ticker);
        }
        pendingIngestions.remove(ticker);
        return ResponseEntity.ok(trades);
    }

    private void triggerIngestion(String ticker) {
        if (pendingIngestions.add(ticker)) {
            String uri = String.format("%s/ingest/%s", ingestionUrl, ticker);
            CompletableFuture.runAsync(() -> {
                try {
                    restTemplate.getForEntity(uri, Void.class);
                    log.info("Ingestion requested for {}", ticker);
                } catch (Exception e) {
                    log.error("Ingestion failed for {}: {}", ticker, e.getMessage(), e);
                    pendingIngestions.remove(ticker);
                }
            });
        }
    }
}
