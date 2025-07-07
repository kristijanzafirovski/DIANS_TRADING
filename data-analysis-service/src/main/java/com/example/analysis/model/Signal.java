package com.example.analysis.model;

import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;

@Table("signals")
public class Signal {

    @PrimaryKey
    private SignalKey key;

    @Column("shortma")
    private double shortMa;

    @Column("longma")
    private double longMa;

    @Column("signal")
    private String signal;

    public Signal() {}

    public Signal(String symbol, long timestamp,
                  double shortMa, double longMa, String signal) {
        this.key     = new SignalKey(symbol, timestamp);
        this.shortMa = shortMa;
        this.longMa  = longMa;
        this.signal  = signal;
    }

    public SignalKey getKey()          { return key; }
    public String    getSymbol()       { return key.getSymbol(); }
    public long      getTimestamp()    { return key.getTimestamp(); }
    public double    getShortMa()      { return shortMa; }
    public double    getLongMa()       { return longMa; }
    public String    getSignal()       { return signal; }

    public void setKey(SignalKey key)         { this.key = key; }
    public void setShortMa(double shortMa)    { this.shortMa = shortMa; }
    public void setLongMa(double longMa)      { this.longMa  = longMa; }
    public void setSignal(String signal)      { this.signal  = signal; }
}
