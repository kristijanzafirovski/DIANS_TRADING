package com.example.ingestion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DataIngestService {
    private static final String BASE_URL = "https://api.twelvedata.com";

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public DataIngestService(RestTemplate restTemplate,
                             @Value("${twelvedata.apikey}") String apiKey,
                             KafkaTemplate<String,String> kafkaTemplate,
                             ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void ingestData(String symbol) {
        log.info("Ingesting data for symbol: {}", symbol);
        Map<LocalDate, DailyStock> daily = fetchDailyData(symbol);
        Map<LocalDateTime, IntradayStock> fiveMin = fetchIntradayData(symbol, "5min");
        Map<LocalDateTime, IntradayStock> hourly = fetchIntradayData(symbol, "1h");

        // publish each entry as JSON to Kafka
        daily.forEach((date, data) -> sendMessage(symbol, date, data));
        fiveMin.forEach((ts, data) -> sendMessage(symbol, ts, data));
        hourly.forEach((ts, data) -> sendMessage(symbol, ts, data));
    }

    private Map<LocalDate, DailyStock> fetchDailyData(String symbol) {
        String url = String.format("%s/time_series?symbol=%s&interval=1day&apikey=%s&outputsize=200",
                BASE_URL, symbol, apiKey);
        TwelveResp resp = restTemplate.getForObject(url, TwelveResp.class);
        if (resp == null || resp.values == null) throw new RuntimeException("Empty daily response");
        Map<LocalDate, DailyStock> map = new HashMap<>();
        resp.values.forEach(v -> {
            LocalDate date = LocalDate.parse(v.datetime);
            map.put(date, new DailyStock(
                    new BigDecimal(v.open), new BigDecimal(v.high),
                    new BigDecimal(v.low), new BigDecimal(v.close),
                    Long.parseLong(v.volume)
            ));
        });
        return map;
    }

    private Map<LocalDateTime, IntradayStock> fetchIntradayData(String symbol, String interval) {
        String url = String.format("%s/time_series?symbol=%s&interval=%s&apikey=%s",
                BASE_URL, symbol, interval, apiKey);
        TwelveResp resp = restTemplate.getForObject(url, TwelveResp.class);
        if (resp == null || resp.values == null) throw new RuntimeException("Empty intraday response");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<LocalDateTime, IntradayStock> map = new HashMap<>();
        resp.values.forEach(v -> {
            LocalDateTime ts = LocalDateTime.parse(v.datetime, fmt);
            IntradayStock data = new IntradayStock(
                    Double.parseDouble(v.open), Double.parseDouble(v.high),
                    Double.parseDouble(v.low), Double.parseDouble(v.close),
                    Long.parseLong(v.volume)
            );
            map.put(ts, data);
        });
        return map;
    }

    private void sendMessage(String symbol, Object time, Object data) {
        try {
            Map<String,Object> msg = Map.of(
                    "symbol", symbol,
                    "timestamp", time.toString(),
                    "data", data
            );
            String json = objectMapper.writeValueAsString(msg);
            kafkaTemplate.send("trades", symbol, json);
            log.info("Sent raw data");
        } catch (Exception e) {
            log.error("Failed to send message", e);
        }
    }

    // DTOs and response wrapper
    private static class TwelveResp { public List<Value> values; static class Value {
        public String datetime, open, high, low, close, volume;
    }
    }

    private static record DailyStock(BigDecimal open, BigDecimal high,
                                     BigDecimal low, BigDecimal close, long volume) {}

    private static record IntradayStock(double open, double high,
                                        double low, double close, long volume) {}
}