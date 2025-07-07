package com.example.analysis.repository;

import com.example.analysis.model.Signal;
import com.example.analysis.model.SignalKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SignalRepository extends CassandraRepository<Signal, SignalKey> {
    @Query("SELECT * FROM signals WHERE symbol = :sym ALLOW FILTERING")
    List<Signal> findByKeySymbol(@Param("sym") String sym);
}
