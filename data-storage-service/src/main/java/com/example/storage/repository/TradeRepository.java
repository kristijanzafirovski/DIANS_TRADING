package com.example.storage.repository;

import com.example.storage.model.Trade;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import java.util.List;

public interface TradeRepository extends CassandraRepository<Trade, String> {
    long countBySymbol(String symbol);
    List<Trade> findBySymbol(String symbol);
    @Query("SELECT DISTINCT symbol FROM trading.trades")
    List<String> findDistinctSymbols();
}