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

    @Query("SELECT DISTINCT symbol FROM trades")
    List<String> findDistinctSymbols();

    @Query("SELECT COUNT(*) FROM trades WHERE symbol = ?0")
    long countBySymbol(String symbol);
}
