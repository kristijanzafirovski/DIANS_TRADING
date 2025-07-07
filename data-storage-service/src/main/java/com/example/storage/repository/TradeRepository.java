// src/main/java/com/example/storage/repository/TradeRepository.java
package com.example.storage.repository;

import com.example.storage.model.Trade;
import com.example.storage.model.TradeKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends CassandraRepository<Trade, TradeKey> {
    List<Trade> findBySymbol(String symbol);
    boolean existsTradeBySymbol(String ticker);

    List<Trade> findByKeySymbol(String symbol);
    boolean    existsByKeySymbol(String symbol);
}
