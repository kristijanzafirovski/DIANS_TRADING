package com.example.storage.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("trades")
public class Trade {

    @PrimaryKey
    private TradeKey key;

    private double price;

    // no-args constructor required by Cassandra mapping
    public Trade() {}

    // the constructor you need
    public Trade(TradeKey key, double price) {
        this.key = key;
        this.price = price;
    }

    public TradeKey getKey() {
        return key;
    }

    public void setKey(TradeKey key) {
        this.key = key;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
