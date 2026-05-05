package com.urlshortener.analyticsservice.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.cql.CqlOperations;
import org.springframework.data.cassandra.core.cql.CqlTemplate;

@Configuration
public class CassandraCqlConfig {

    @Bean
    public CqlOperations cqlOperations(CqlSession session) {
        return new CqlTemplate(session);
    }
}

