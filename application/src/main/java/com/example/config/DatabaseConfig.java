package com.example.config;

import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
public class DatabaseConfig {

    private final String dbClusterArn;
    private final String dbSecretArn;
    private final String dbName;

    public DatabaseConfig() {
        this.dbClusterArn = System.getenv("DB_CLUSTER_ARN");
        this.dbSecretArn = System.getenv("DB_SECRET_ARN");
        this.dbName = System.getenv("DB_NAME");

        validateConfig();
    }

    public void validateConfig() {
        if (Stream.of(dbClusterArn, dbSecretArn, dbName).anyMatch(Objects::isNull)) {
            throw new IllegalStateException("One or more database configuration environment variables are not set!");
        }
    }
}
