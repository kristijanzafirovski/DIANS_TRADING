package com.example.storage.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;

@Table("trades")
public class Trade {
    @PrimaryKeyColumn(name = "symbol", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String symbol;

    @PrimaryKeyColumn(name = "timestamp", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private long timestamp;

    private double price;

    public Trade() {}

    public Trade(String symbol, long timestamp, double price) {
        this.symbol = symbol;
        this.timestamp = timestamp;
        this.price = price;
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}