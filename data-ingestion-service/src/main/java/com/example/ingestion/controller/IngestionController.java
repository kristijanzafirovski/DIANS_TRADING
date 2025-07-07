package com.example.ingestion.controller;

import com.example.ingestion.service.DataIngestService;
import jakarta.websocket.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingest")
@CrossOrigin(origins = "*")
public class IngestionController {
    private static final Logger log = LoggerFactory.getLogger(IngestionController.class);
    private final DataIngestService ingestService;

    public IngestionController(DataIngestService ingestService) {
        this.ingestService = ingestService;
    }

    @GetMapping("/{symbol}")
    public String ingest(@PathVariable("symbol") String symbol) {
        ingestService.ingestData(symbol);
        log.info("Ingesting data for symbol: " + symbol);
        return "Ingestion triggered for " + symbol;
    }
}