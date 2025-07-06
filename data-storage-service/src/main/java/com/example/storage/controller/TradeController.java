package com.example.storage.controller;

import com.example.storage.model.Trade;
import com.example.storage.model.TradeKey;
import com.example.storage.repository.TradeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TradeController {

    private final TradeRepository repository;

    public TradeController(TradeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/data")
    public List<Trade> getAllTrades() {
        return repository.findAll();
    }

    @GetMapping("/symbols/{ticker}")
    public List<Trade> getTickerTrade(@PathVariable("ticker") String ticker){
        return repository.findBySymbol(ticker);
    }
}
