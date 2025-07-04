package com.example.analysis.model;

public class OhlcMessage {
    private String symbol;
    private String timestamp;
    private Ohlc data;
    // getters & setters…

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Ohlc getData() {
        return data;
    }

    public void setData(Ohlc data) {
        this.data = data;
    }
}