package com.example.storage.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

@Table("trades")
public class Trade {

    @PrimaryKeyColumn(name = "symbol",
            ordinal = 0,
            type = PrimaryKeyType.PARTITIONED)
    private String symbol;

    @PrimaryKeyColumn(name = "timestamp",
            ordinal = 1,
            type = PrimaryKeyType.CLUSTERED)
    private long timestamp;

    @Column("open")
    private double open;

    @Column("high")
    private double high;

    @Column("low")
    private double low;

    @Column("close")
    private double close;

    @Column("volume")
    private long volume;

    public Trade() {}

    public Trade(
            String symbol,
            long timestamp,
            double open,
            double high,
            double low,
            double close,
            long volume
    ) {
        this.symbol    = symbol;
        this.timestamp = timestamp;
        this.open      = open;
        this.high      = high;
        this.low       = low;
        this.close     = close;
        this.volume    = volume;
    }

    // getters & setters omitted for brevity
}
