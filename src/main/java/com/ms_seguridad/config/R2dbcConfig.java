package com.ms_seguridad.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.lang.NonNull;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.pool.PoolingConnectionFactoryProvider;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Configuration
@Slf4j
public class R2dbcConfig  extends AbstractR2dbcConfiguration {

    @Value("${dev.datasource.host}")
    private String host;

    @Value("${dev.datasource.username}")
    private String username;

    @Value("${dev.datasource.password}")
    private String password;

    @Value("${dev.datasource.databaseName}")
    private String databaseName;

    @Value("${dev.datasource.port}")
    private int port;

    @Value("${dev.datasource.initialSize}")
    private int initialSize;

    @Value("${dev.datasource.maximumPoolSize}")
    private int maxSize;

    @Value("${dev.datasource.maxIdle}")
    private int maxIdleTime;

    @Value("${dev.datasource.minIdle}")
    private int minIdle;
    @Override
    @Bean
    public @NonNull ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory = new PoolingConnectionFactoryProvider().create(
                ConnectionFactoryOptions.builder()
                        .option(ConnectionFactoryOptions.PROTOCOL, "mysql")
                        .option(ConnectionFactoryOptions.HOST, host)
                        .option(ConnectionFactoryOptions.PORT, port)
                        .option(ConnectionFactoryOptions.USER, username)
                        .option(ConnectionFactoryOptions.PASSWORD, password)
                        .option(ConnectionFactoryOptions.DATABASE, databaseName)
                        .option(ConnectionFactoryOptions.SSL, false)
                        .build()
        );

        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
                .initialSize(initialSize)
                .maxSize(maxSize)
                .minIdle(minIdle)
                .maxIdleTime(Duration.ofMinutes(maxIdleTime))
                .build();

        return new ConnectionPool(configuration);
    }    
}