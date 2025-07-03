package com.example.processing.model;

import java.io.Serializable;

public class Trade implements Serializable {
    private String symbol;
    private double price;
    private long timestamp;

    public Trade() {}

    public Trade(String symbol, double price, long timestamp) {
        this.symbol = symbol;
        this.price     = price;
        this.timestamp = timestamp;
    }

     public String getSymbol()          { return symbol; }
    public void   setSymbol(String s)  { this.symbol = s; }

    public double getPrice()           { return price; }
    public void   setPrice(double p)   { this.price = p; }

    public long   getTimestamp()       { return timestamp; }
    public void   setTimestamp(long t) { this.timestamp = t; }
}
