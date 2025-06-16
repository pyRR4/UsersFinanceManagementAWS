package com.example.repository.implementation;

import com.example.config.DatabaseConfig;
import com.example.repository.contract.UserRepository;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final RdsDataClient rdsDataClient;
    private final DatabaseConfig databaseConfig;

    @Override
    public Optional<Integer> findUserIdByCognitoSub(String cognitoSub) {
        String sql = "SELECT id FROM users WHERE cognito_sub = :cognito_sub";
        SqlParameter param = SqlParameter.builder().name("cognito_sub").value(Field.builder().stringValue(cognitoSub).build()).build();

        ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                .resourceArn(databaseConfig.getDbClusterArn())
                .secretArn(databaseConfig.getDbSecretArn())
                .database(databaseConfig.getDbName())
                .sql(sql)
                .parameters(param)
                .build();

        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        if (!response.records().isEmpty()) {
            long userId = response.records().get(0).get(0).longValue();
            return Optional.of((int) userId);
        }
        return Optional.empty();
    }

    @Override
    public int createUser(String cognitoSub, String email) {
        String sql = "INSERT INTO users (cognito_sub, email) VALUES (:cognito_sub, :email) RETURNING id";

        SqlParameter subParam = SqlParameter.builder().name("cognito_sub").value(Field.builder().stringValue(cognitoSub).build()).build();
        SqlParameter emailParam = SqlParameter.builder().name("email").value(Field.builder().stringValue(email).build()).build();

        ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                .resourceArn(databaseConfig.getDbClusterArn())
                .secretArn(databaseConfig.getDbSecretArn())
                .database(databaseConfig.getDbName())
                .sql(sql)
                .parameters(subParam, emailParam)
                .build();

        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);
        long newUserId = response.records().get(0).get(0).longValue();
        return (int) newUserId;
    }
}
