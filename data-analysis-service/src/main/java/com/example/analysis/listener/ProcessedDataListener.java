package com.example.analysis.listener;

import com.example.analysis.model.OhlcMessage;
import com.example.analysis.model.Signal;
import com.example.analysis.repository.SignalRepository;
import com.example.analysis.service.SignalGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessedDataListener {

    private final ObjectMapper mapper = new ObjectMapper();
    private final SignalGenerationService service;
    private final SignalRepository repository;

    public ProcessedDataListener(SignalGenerationService service,
                                 SignalRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @KafkaListener(topics = "processed-data", groupId = "analysis-group")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            OhlcMessage msg = mapper.readValue(record.value(), OhlcMessage.class);
            Signal sig = service.generateSignal(msg);
            repository.save(sig);
        } catch (Exception e) {
            // log & skip
            System.err.println("Failed to handle record: " + record.value());
            e.printStackTrace();
        }
    }
}
