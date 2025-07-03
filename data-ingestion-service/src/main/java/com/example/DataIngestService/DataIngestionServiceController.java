package com.example.DataIngestService;

import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.*;

@RestController
@RequestMapping("/ingest")
public class DataIngestionServiceController {
    private final KafkaTemplate<String,String> kafka;

    public DataIngestionServiceController(KafkaTemplate<String,String> kafka) {{
        this.kafka = kafka;
    }}

    @GetMapping("/{symbol}")
    public Map<String,Object> ingest(@PathVariable String symbol) {{
        double price = new Random().nextDouble()*100;
        long ts = System.currentTimeMillis();
        String msg = String.format("{\"symbol\":\"%s\",\"price\":%.2f,\"timestamp\":%d}", symbol, price, ts);
        kafka.send("raw-data", msg);
        return Map.of("status","sent","data",Map.of("symbol",symbol,"price",price,"timestamp",ts));
    }}
}
