package com.example.storage.controller;

import com.example.storage.model.Trade;
import com.example.storage.repository.TradeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trades")
public class StorageController {

    private final TradeRepository repo;

    public StorageController(TradeRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Trade> all() {
        return repo.findAll();
    }

    @GetMapping("/symbols")
    public List<String> symbols() {
        return repo.findDistinctSymbols();
    }

    @GetMapping("/{symbol}/count")
    public long count(@PathVariable String symbol) {
        return repo.countBySymbol(symbol);
    }
}
