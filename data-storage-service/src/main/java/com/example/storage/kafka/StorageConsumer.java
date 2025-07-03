package com.example.storage.kafka;

import com.example.storage.model.Trade;
import com.example.storage.model.TradeKey;
import com.example.storage.repository.TradeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StorageConsumer {

    private final TradeRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public StorageConsumer(TradeRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "processed-data", groupId = "storage-group")
    public void onMessage(String message) throws Exception {
        JsonNode node = mapper.readTree(message);
        String symbol = node.get("symbol").asText();
        double price   = node.get("price").asDouble();
        long ts        = node.get("timestamp").asLong();

        TradeKey key = new TradeKey(symbol, ts);
        repository.save(new Trade(key, price));
    }
}
