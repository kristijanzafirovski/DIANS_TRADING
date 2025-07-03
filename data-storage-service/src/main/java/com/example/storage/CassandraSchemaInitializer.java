package com.example.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import com.datastax.oss.driver.api.core.CqlSession;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
public class CassandraSchemaInitializer implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private CqlSession session;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            Resource resource = resourceLoader.getResource("classpath:schema.cql");
            String cql = new BufferedReader(new InputStreamReader(resource.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
            for (String stmt : cql.split(";")) {
                if (!stmt.trim().isEmpty()) session.execute(stmt + ";");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Cassandra schema", e);
        }
    }
}
