package com.example.ingestion;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/ingest")
public class IngestionController {
    private final KafkaTemplate<String,String> kafkaTemplate;

    public IngestionController(KafkaTemplate<String,String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/{symbol}")
    public Map<String,Object> ingest(@PathVariable String symbol) {
        double price = new Random().nextDouble()*100;
        long ts = System.currentTimeMillis();
        String msg = String.format("{\"symbol\":\"%s\",\"price\":%.2f,\"timestamp\":%d}", symbol, price, ts);
        kafkaTemplate.send("raw-data", msg);
        return Map.of("status","sent","data",Map.of("symbol",symbol,"price",price,"timestamp",ts));
    }
}
