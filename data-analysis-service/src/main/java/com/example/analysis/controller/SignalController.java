package com.example.analysis.controller;

import com.example.analysis.model.Signal;
import com.example.analysis.repository.SignalRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/signals")
public class SignalController {
    private final SignalRepository repo;

    public SignalController(SignalRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{symbol}")
    public List<Signal> getLatest(@PathVariable String symbol,
                                  @RequestParam(defaultValue="10") int limit) {
        return repo.findBySymbolOrderByTimestampDesc(symbol)
                .stream().limit(limit).toList();
    }
}
