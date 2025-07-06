package com.example.analysis.controller;

import com.example.analysis.model.Signal;
import com.example.analysis.repository.SignalRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class SignalController {

    private final SignalRepository repository;

    public SignalController(SignalRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/signals")
    public List<Signal> getSignals() {
        return repository.findAll();
    }
    @GetMapping("/symbol/{ticker}")
    public List<Signal> getSignalsByTicker(String ticker){
        return repository.findBySymbolOrderByTimestampDesc(ticker);
    }
}
