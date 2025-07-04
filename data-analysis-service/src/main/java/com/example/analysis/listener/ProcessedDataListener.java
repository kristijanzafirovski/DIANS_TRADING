package com.example.analysis.listener;

import com.example.analysis.model.OhlcMessage;
import com.example.analysis.model.Signal;
import com.example.analysis.service.SignalGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ProcessedDataListener {

    private final ObjectMapper mapper = new ObjectMapper();
    private final SignalGenerationService service;
    private final List<Signal> signals = new CopyOnWriteArrayList<>();

    public ProcessedDataListener(SignalGenerationService service) {
        this.service = service;
    }

    @KafkaListener(
            topics = "processed-data",
            groupId = "analysis-group"
    )
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            // parse the JSON into our window‐aggregate DTO
            OhlcMessage aw = mapper.readValue(record.value(), OhlcMessage.class);
            Signal sig = service.generateSignal(aw);
            signals.add(sig);
        } catch (Exception e) {
            // log and skip bad records
            System.err.println("Failed to parse/process window aggregate: " + record.value());
            e.printStackTrace();
        }
    }

    /** Expose signals to the controller */
    public List<Signal> getSignals() {
        return signals;
    }
}
