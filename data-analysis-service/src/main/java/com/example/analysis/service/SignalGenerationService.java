package com.example.analysis.service;

import com.example.analysis.model.Trade;
import com.example.analysis.model.Signal;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.LinkedList;

@Service
public class SignalGenerationService {

    // simple sliding‐window MA buffers
    private final Deque<Double> shortWindow = new LinkedList<>();
    private final Deque<Double> longWindow = new LinkedList<>();

    private final int SHORT_N = 5, LONG_N = 14;

    public Signal generate(Trade trade) {
        updateWindow(shortWindow, trade.getPrice(), SHORT_N);
        updateWindow(longWindow, trade.getPrice(), LONG_N);

        double shortMa = shortWindow.stream().mapToDouble(d->d).average().orElse(trade.getPrice());
        double longMa  = longWindow.stream().mapToDouble(d->d).average().orElse(trade.getPrice());

        String sig;
        if (shortMa > longMa) sig = "BUY";
        else if (shortMa < longMa) sig = "SELL";
        else sig = "HOLD";

        return new Signal(trade.getSymbol(),
                trade.getTimestamp(),
                shortMa, longMa, sig);
    }

    private void updateWindow(Deque<Double> w, double price, int maxSize) {
        if (w.size() == maxSize) w.pollFirst();
        w.offerLast(price);
    }
}
