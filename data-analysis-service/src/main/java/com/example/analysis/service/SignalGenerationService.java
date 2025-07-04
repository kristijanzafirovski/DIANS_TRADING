package com.example.analysis.service;

import com.example.analysis.model.OhlcMessage;
import com.example.analysis.model.Signal;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.LinkedList;

@Service
public class SignalGenerationService {

    private static final int SHORT_WINDOW_SIZE = 5;   // last 5 bars
    private static final int LONG_WINDOW_SIZE  = 30;  // last 30 bars

    private final Deque<Double> shortWindow = new LinkedList<>();
    private final Deque<Double> longWindow  = new LinkedList<>();

    // matches the Spark output "yyyy-MM-dd'T'HH:mm"
    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    /**
     * Generate a BUY/SELL/HOLD signal based on SMA crossover of the bar's average price.
     */
    public Signal generateSignal(OhlcMessage msg) {
        // 1) compute the bar's average price
        double open  = msg.getData().getOpen();
        double high  = msg.getData().getHigh();
        double low   = msg.getData().getLow();
        double close = msg.getData().getClose();
        double avgPrice = (open + high + low + close) / 4.0;

        // 2) update moving-average buffers
        addPrice(shortWindow, avgPrice, SHORT_WINDOW_SIZE);
        addPrice(longWindow,  avgPrice, LONG_WINDOW_SIZE);

        // 3) compute SMAs
        double shortMa = shortWindow.stream().mapToDouble(d -> d).average().orElse(avgPrice);
        double longMa  = longWindow.stream(). mapToDouble(d -> d).average().orElse(avgPrice);

        // 4) decide signal
        String signal;
        if (shortMa > longMa)      signal = "BUY";
        else if (shortMa < longMa) signal = "SELL";
        else                        signal = "HOLD";

        // 5) parse the bar's timestamp (end of window) into epoch millis
        LocalDateTime ldt = LocalDateTime.parse(msg.getTimestamp(), TS_FMT);
        long ts = ldt.toInstant(ZoneOffset.UTC).toEpochMilli();

        return new Signal(
                msg.getSymbol(),
                ts,
                shortMa,
                longMa,
                signal
        );
    }

    /** maintain a fixed-size FIFO for moving average */
    private void addPrice(Deque<Double> window, double price, int maxSize) {
        if (window.size() >= maxSize) {
            window.pollFirst();
        }
        window.offerLast(price);
    }
}
