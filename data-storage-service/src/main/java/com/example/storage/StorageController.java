package com.example.storage;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@RestController
@RequestMapping("/data")
public class StorageController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<DataPoint> getAll() {
        return jdbcTemplate.query(
            "SELECT symbol, price, timestamp FROM data",
            (rs, rowNum) -> new DataPoint(
                rs.getString("symbol"),
                rs.getDouble("price"),
                rs.getLong("timestamp")
            )
        );
    }
}
