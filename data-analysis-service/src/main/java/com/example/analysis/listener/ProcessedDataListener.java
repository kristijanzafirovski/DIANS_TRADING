package com.example.analysis.listener;

import com.example.analysis.model.Trade;
import com.example.analysis.model.Signal;
import com.example.analysis.repository.SignalRepository;
import com.example.analysis.service.SignalGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProcessedDataListener {

    private final SignalGenerationService gen;
    private final SignalRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    public ProcessedDataListener(SignalGenerationService gen,
                                 SignalRepository repo) {
        this.gen = gen;
        this.repo = repo;
    }

    @KafkaListener(topics = "processed-data", groupId = "analysis-service")
    public void onMessage(String message) throws Exception {
        Trade trade = mapper.readValue(message, Trade.class);
        Signal signal = gen.generate(trade);
        repo.save(signal);
    }
}
