package com.example.analysis.service;

import com.example.analysis.model.OhlcMessage;
import com.example.analysis.model.Signal;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.LinkedList;

@Service
public class SignalGenerationService {
    private static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private static final int SHORT_WINDOW_SIZE = 5;
    private static final int LONG_WINDOW_SIZE  = 30;

    private final Deque<Double> shortWindow = new LinkedList<>();
    private final Deque<Double> longWindow  = new LinkedList<>();

    /**
     * Compute SMA crossover on the *average* of open/high/low/close from each bar.
     */
    public Signal generateSignal(OhlcMessage msg) {
        // 1) compute the bar's average price
        double open  = msg.getData().getOpen();
        double high  = msg.getData().getHigh();
        double low   = msg.getData().getLow();
        double close = msg.getData().getClose();
        double avgPrice = (open + high + low + close) / 4.0;

        // 2) update our rolling‐average buffers
        roll(shortWindow, avgPrice, SHORT_WINDOW_SIZE);
        roll(longWindow,  avgPrice, LONG_WINDOW_SIZE);

        // 3) compute each SMA
        double shortMa = shortWindow.stream().mapToDouble(d -> d).average().orElse(avgPrice);
        double longMa  = longWindow.stream(). mapToDouble(d -> d).average().orElse(avgPrice);

        // 4) decide signal
        String sig = shortMa > longMa   ? "BUY"
                : shortMa < longMa   ? "SELL"
                : "HOLD";

        // 5) parse your ISO‐timestamp into epoch millis
        LocalDateTime ldt = LocalDateTime.parse(msg.getTimestamp(), fmt);
        long ts = ldt.toInstant(ZoneOffset.UTC).toEpochMilli();

        return new Signal(
                msg.getSymbol(),
                ts,
                shortMa,
                longMa,
                sig
        );
    }

    private void roll(Deque<Double> window, double price, int maxSize) {
        if (window.size() >= maxSize) window.pollFirst();
        window.offerLast(price);
    }
}
