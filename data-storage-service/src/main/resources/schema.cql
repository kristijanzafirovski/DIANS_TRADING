CREATE KEYSPACE IF NOT EXISTS trading WITH replication = {'class':'SimpleStrategy','replication_factor':1};
CREATE TABLE IF NOT EXISTS trading.trades (
                                      symbol    text,
                                      timestamp bigint,
                                      open      double,
                                      high      double,
                                      low       double,
                                      close     double,
                                      volume    bigint,
                                      PRIMARY KEY ((symbol), timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);