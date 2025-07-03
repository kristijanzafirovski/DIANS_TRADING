package com.example.ingestion.controller;

import com.example.ingestion.service.DataIngestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingest")
public class IngestionController {
    private final DataIngestService ingestService;

    public IngestionController(DataIngestService ingestService) {
        this.ingestService = ingestService;
    }

    @GetMapping("/{symbol}")
    public String ingest(@PathVariable String symbol) {
        ingestService.ingestData(symbol);
        return "Ingestion triggered for " + symbol;
    }
}