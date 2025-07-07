// SignalKey.java
package com.example.analysis.model;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@PrimaryKeyClass
public class SignalKey implements Serializable {
    @PrimaryKeyColumn(name = "symbol",
            ordinal = 0,
            type = PrimaryKeyType.PARTITIONED)
    private String symbol;

    @PrimaryKeyColumn(name = "timestamp",
            ordinal = 1,
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING)
    private long timestamp;

    public SignalKey() {}
    public SignalKey(String symbol, long timestamp) {
        this.symbol = symbol;
        this.timestamp = timestamp;
    }
    public String getSymbol()    { return symbol; }
    public long   getTimestamp() { return timestamp; }
    public void   setSymbol(String s)    { this.symbol = s; }
    public void   setTimestamp(long t)   { this.timestamp = t; }
}
