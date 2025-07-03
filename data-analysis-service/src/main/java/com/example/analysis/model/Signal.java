package com.example.analysis.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("signals")
public class Signal {
    @PrimaryKey
    private UUID id;
    private String symbol;
    private long timestamp;
    private double shortMa;
    private double longMa;
    private String signal;   // e.g. "BUY", "SELL", "HOLD"

    public Signal() {}

    public Signal(String symbol, long timestamp,
                  double shortMa, double longMa, String signal) {
        this.id = UUID.randomUUID();
        this.symbol = symbol;
        this.timestamp = timestamp;
        this.shortMa = shortMa;
        this.longMa = longMa;
        this.signal = signal;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getShortMa() {
        return shortMa;
    }

    public void setShortMa(double shortMa) {
        this.shortMa = shortMa;
    }

    public double getLongMa() {
        return longMa;
    }

    public void setLongMa(double longMa) {
        this.longMa = longMa;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }
}
