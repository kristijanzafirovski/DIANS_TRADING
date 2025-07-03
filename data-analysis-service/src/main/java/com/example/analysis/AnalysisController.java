package com.example.analysis;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {
    private final AnalysisService service;

    public AnalysisController(AnalysisService service) {
        this.service = service;
    }

    @GetMapping(value = "/trend/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Trend> trend(@PathVariable String symbol) {
        return service.fetchData(symbol)
                .map(service::computeTrend);
    }
    @GetMapping("/analysis/data/{symbol}")
    public Flux<DataPoint> data(@PathVariable String symbol) {
        return service
                .fetchData(symbol)
                .flatMapMany(Flux::fromIterable);
    }

}
