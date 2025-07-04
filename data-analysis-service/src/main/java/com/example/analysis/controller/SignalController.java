package com.example.analysis.controller;

import com.example.analysis.listener.ProcessedDataListener;
import com.example.analysis.model.Signal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SignalController {

    private final ProcessedDataListener listener;

    public SignalController(ProcessedDataListener listener) {
        this.listener = listener;
    }

    @GetMapping("/signals")
    public List<Signal> getSignals() {
        return listener.getSignals();
    }
}
