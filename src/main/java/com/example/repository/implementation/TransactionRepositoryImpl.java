package com.example.repository.implementation;

import com.example.config.DatabaseConfig;
import com.example.model.TransactionRequest;
import com.example.repository.contract.TransactionRepository;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final RdsDataClient rdsDataClient;
    private final DatabaseConfig databaseConfig;

    @Override
    public void save(TransactionRequest transaction, int userId) {
        String sql = "INSERT INTO transactions (user_id, amount, date, description, category_id) " +
                "VALUES (:user_id, :amount, :date, :description, :category_id)";

        List<SqlParameter> parameters = new ArrayList<>();
        parameters.add(SqlParameter.builder().name("user_id").value(Field.builder().longValue((long) userId).build()).build());
        parameters.add(SqlParameter.builder().name("amount").value(Field.builder().doubleValue(transaction.getAmount()).build()).build());
        parameters.add(SqlParameter.builder().name("date").value(Field.builder().stringValue(transaction.getDate()).build()).build());
        parameters.add(SqlParameter.builder().name("description").value(Field.builder().stringValue(transaction.getDescription()).build()).build());
        parameters.add(SqlParameter.builder().name("category_id").value(Field.builder().longValue((long) transaction.getCategoryId()).build()).build());

        ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                .resourceArn(databaseConfig.getDbClusterArn())
                .secretArn(databaseConfig.getDbSecretArn())
                .database(databaseConfig.getDbName())
                .sql(sql)
                .parameters(parameters)
                .build();

        rdsDataClient.executeStatement(request);
    }
}