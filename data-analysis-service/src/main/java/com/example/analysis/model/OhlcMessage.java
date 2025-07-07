package com.example.analysis.model;

public class OhlcMessage {
    private String symbol;
    private String timestamp;
    private BarData data;
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

    public BarData getData() {
        return data;
    }

    public void setData(BarData data) {
        this.data = data;
    }


}
