package com.example.storage.dto;

public class RawTradeMessage {
    private QuoteData data;
    private String symbol;
    private String timestamp;  // e.g. "2025-07-03T09:30"

    public RawTradeMessage() {}

    // getters & setters
    public QuoteData getData()       { return data; }
    public void setData(QuoteData d) { this.data = d; }
    public String getSymbol()        { return symbol; }
    public void setSymbol(String s)  { this.symbol = s; }
    public String getTimestamp()     { return timestamp; }
    public void setTimestamp(String t) { this.timestamp = t; }
}
