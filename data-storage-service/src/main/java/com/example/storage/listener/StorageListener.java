package com.example.storage.listener;

import com.example.storage.dto.RawTradeMessage;
import com.example.storage.model.Trade;
import com.example.storage.repository.TradeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.*;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Component
public class StorageListener {
    private static final Logger log = LoggerFactory.getLogger(StorageListener.class);

    private final TradeRepository repo;
    private final ObjectMapper mapper;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public StorageListener(TradeRepository repo, ObjectMapper mapper) {
        this.repo   = repo;
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = "processed-data",
            groupId = "storage-group"
    )
    public void onMessage(ConsumerRecord<String,String> record) {
        try {
            RawTradeMessage msg = mapper.readValue(record.value(), RawTradeMessage.class);
            // parse timestamp (assume UTC)
            LocalDateTime ldt = LocalDateTime.parse(msg.getTimestamp(), fmt);
            long ts = ldt.toInstant(ZoneOffset.UTC).toEpochMilli();

            // extract every quote field
            double o     = msg.getData().getOpen();
            double h     = msg.getData().getHigh();
            double l     = msg.getData().getLow();
            double c     = msg.getData().getClose();
            long   vol   = msg.getData().getVolume();

            Trade trade = new Trade(
                    msg.getSymbol(),
                    ts,
                    o, h, l, c, vol
            );

            repo.save(trade);
        } catch (Exception e) {
            log.error("Failed to parse or save trade: {}", record.value(), e);
        }
    }
}
