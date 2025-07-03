package com.example.analysis;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalysisService {
    private final WebClient client;

    public AnalysisService(WebClient.Builder builder) {
        this.client = builder.baseUrl("http://data-storage-service:8000").build();
    }

    public Mono<List<DataPoint>> fetchData(String symbol) {
        return client.get()
                .uri("/data")
                .retrieve()
                .bodyToFlux(DataPoint.class)
                .filter(dp -> symbol == null || dp.getSymbol().equals(symbol))
                .collectList();
    }

    public Trend computeTrend(List<DataPoint> data) {
        int n = data.size();
        if (n == 0) return new Trend(0, 0);

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (DataPoint dp : data) {
            double x = dp.getTimestamp();
            double y = dp.getPrice();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;
        return new Trend(slope, intercept);
    }
}
