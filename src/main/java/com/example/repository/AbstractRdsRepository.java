package com.example.repository;

import com.example.config.DatabaseConfig;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractRdsRepository<T> {

    protected final RdsDataClient rdsDataClient;
    protected final DatabaseConfig databaseConfig;

    protected ExecuteStatementRequest createExecuteStatementRequest(String sql, SqlParameter... parameters) {
        return ExecuteStatementRequest.builder()
                .resourceArn(databaseConfig.getDbClusterArn())
                .secretArn(databaseConfig.getDbSecretArn())
                .database(databaseConfig.getDbName())
                .sql(sql)
                .parameters(parameters)
                .build();
    }

    protected List<T> mapResponseToList(ExecuteStatementResponse response) {
        return response.records()
                .stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    protected abstract T mapToEntity(List<Field> record);
}
