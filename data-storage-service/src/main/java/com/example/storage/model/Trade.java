package com.example.storage.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;

@Table("trades")
public class Trade {
    @PrimaryKey
    private TradeKey key;

    @Column("open")   private double open;
    @Column("high")   private double high;
    @Column("low")    private double low;
    @Column("close")  private double close;
    @Column("volume") private long   volume;

    public Trade() {}

    public Trade(String symbol, long ts,
                 double o, double h,
                 double l, double c, long v) {
        this.key    = new TradeKey(symbol, ts);
        this.open   = o;  this.high  = h;
        this.low    = l;  this.close = c;
        this.volume = v;
    }

    // Expose key parts at top‐level
    public String getSymbol()    { return key.getSymbol();    }
    public long   getTimestamp() { return key.getTimestamp(); }



    public double getOpen()      { return open;      }
    public double getHigh()      { return high;      }
    public double getLow()       { return low;       }
    public double getClose()     { return close;     }
    public long   getVolume()    { return volume;    }

    public void setOpen(double open)          { this.open = open;           }
    public void setHigh(double high)          { this.high = high;           }
    public void setLow(double low)            { this.low = low;             }
    public void setClose(double close)        { this.close = close;         }
    public void setVolume(long volume)        { this.volume = volume;       }
}
