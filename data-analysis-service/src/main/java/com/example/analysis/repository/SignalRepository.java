package com.example.analysis.repository;

import com.example.analysis.model.Signal;
import org.springframework.data.cassandra.repository.CassandraRepository;
import java.util.List;
import java.util.UUID;

public interface SignalRepository extends CassandraRepository<Signal, UUID> {
    List<Signal> findBySymbolOrderByTimestampDesc(String symbol);
}
