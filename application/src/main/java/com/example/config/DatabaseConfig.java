package com.example.config;

import lombok.Getter;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
public class DatabaseConfig {

    private final String dbHost;
    private final int dbPort;
    private final String dbName;
    private final String dbUsername;
    private final String dbPasswordSecretArn;

    public DatabaseConfig() {
        this.dbHost = System.getenv("DB_HOST");
        this.dbPort = Integer.parseInt(System.getenv("DB_PORT"));
        this.dbName = System.getenv("DB_NAME");
        this.dbUsername = System.getenv("DB_USERNAME");
        this.dbPasswordSecretArn = System.getenv("DB_SECRET_ARN");

        validateConfig();
    }

    private void validateConfig() {
        if (Stream.of(dbHost, dbName, dbUsername, dbPasswordSecretArn).anyMatch(Objects::isNull)) {
            throw new IllegalStateException("One or more database configuration environment variables are not set!");
        }
    }
}