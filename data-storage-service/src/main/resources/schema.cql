CREATE KEYSPACE IF NOT EXISTS trading WITH replication = {'class':'SimpleStrategy','replication_factor':1};
CREATE TABLE IF NOT EXISTS trading.trades (
                                              symbol text,
                                              price double,
                                              timestamp bigint,
                                              PRIMARY KEY ((symbol), timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);
