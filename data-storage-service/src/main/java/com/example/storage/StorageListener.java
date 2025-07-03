package com.example.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.datastax.oss.driver.api.core.CqlSession;

import java.net.InetSocketAddress;

@Component
public class StorageListener {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CqlSession cassandra;

    private ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "processed-data", groupId = "storage")
    public void listen(String msg) {
        try {
            DataPoint dp = mapper.readValue(msg, DataPoint.class);
            // insert into Postgres
            jdbcTemplate.update(
                "INSERT INTO data(symbol, timestamp, price) VALUES (?, ?, ?)",
                dp.getSymbol(), dp.getTimestamp(), dp.getPrice()
            );
            // insert into Cassandra
            cassandra.execute(
                "INSERT INTO trading.data (symbol, timestamp, price) VALUES (?, ?, ?)",
                dp.getSymbol(), dp.getTimestamp(), dp.getPrice()
            );
        } catch (Exception e) {
            // log or handle parsing errors
            e.printStackTrace();
        }
    }
}
