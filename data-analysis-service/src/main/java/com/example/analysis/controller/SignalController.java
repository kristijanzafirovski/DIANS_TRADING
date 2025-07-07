package com.example.analysis.controller;

import com.example.analysis.model.Signal;
import com.example.analysis.repository.SignalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class SignalController {

    private static final Logger log = LoggerFactory.getLogger(SignalController.class);
    private final SignalRepository repository;

    public SignalController(SignalRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/signals")
    public List<Signal> getSignals() {
        log.info("Get all signals");
        return repository.findAll();
    }
    @GetMapping("/symbol/{ticker}")
    public List<Signal> getSignalsByTicker(@PathVariable("ticker")String ticker){
        log.info("Get signals by ticker: {}", ticker);
        return repository.findByKeySymbol(ticker);

    }
}
