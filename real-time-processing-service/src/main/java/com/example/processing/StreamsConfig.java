package com.example.processing;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.List;

@Configuration
@EnableKafkaStreams
public class StreamsConfig {

    @Bean
    public KStream<String, String> kStream(StreamsBuilder builder) {
        ObjectMapper mapper = new ObjectMapper();

        KStream<String, String> rawStream = builder.stream(
            "raw-data",
            Consumed.with(Serdes.String(), Serdes.String())
        );

        rawStream
            .filter((key, value) -> value != null && !value.isEmpty())
            .flatMap((key, value) -> {
                try {
                    JsonNode node = mapper.readTree(value);
                    String symbol = node.get("symbol").asText();
                    double price = node.get("price").asDouble();
                    long timestamp = node.get("timestamp").asLong();

                    ObjectNode outNode = mapper.createObjectNode();
                    outNode.put("symbol", symbol);
                    outNode.put("price", price);
                    outNode.put("timestamp", timestamp);
                    outNode.put("processedAt", System.currentTimeMillis());

                    String outJson = mapper.writeValueAsString(outNode);
                    return List.of(new KeyValue<>(symbol, outJson));
                } catch (Exception e) {
                    // log and skip malformed records
                    e.printStackTrace();
                    return List.of();
                }
            })
            .to("processed-data", Produced.with(Serdes.String(), Serdes.String()));

        return rawStream;
    }
}
