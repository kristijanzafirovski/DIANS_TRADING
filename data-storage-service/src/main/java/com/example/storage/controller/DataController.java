// src/main/java/com/example/storage/controller/DataController.java
package com.example.storage.controller;

import com.example.storage.model.Trade;
import com.example.storage.repository.TradeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data")
public class DataController {
    private final TradeRepository repo;
    public DataController(TradeRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Trade> allTrades() {
        return repo.findAll();
    }

    @GetMapping("/symbols")
    public List<String> symbols() {
        return repo.findDistinctSymbols();
    }
}
