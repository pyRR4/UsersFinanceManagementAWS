package com.example.service.implementation;

import com.example.config.DatabaseConfig;
import com.example.service.contract.TransactionManager;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.BeginTransactionRequest;
import software.amazon.awssdk.services.rdsdata.model.BeginTransactionResponse;
import software.amazon.awssdk.services.rdsdata.model.CommitTransactionRequest;
import software.amazon.awssdk.services.rdsdata.model.RollbackTransactionRequest;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class RdsTransactionManager implements TransactionManager {

    private final RdsDataClient rdsDataClient;
    private final DatabaseConfig databaseConfig;

    @Override
    public void execute(Consumer<String> transactionalLogic) {
        String transactionId = null;

        try {
            BeginTransactionRequest beginTransactionRequest = BeginTransactionRequest.builder()
                    .resourceArn(databaseConfig.getDbClusterArn())
                    .secretArn(databaseConfig.getDbSecretArn())
                    .database(databaseConfig.getDbName())
                    .build();
            BeginTransactionResponse beginResponse = rdsDataClient.beginTransaction(beginTransactionRequest);
            transactionId = beginResponse.transactionId();

            transactionalLogic.accept(transactionId);

            CommitTransactionRequest commitRequest = CommitTransactionRequest.builder()
                    .resourceArn(databaseConfig.getDbClusterArn())
                    .secretArn(databaseConfig.getDbSecretArn())
                    .transactionId(transactionId)
                    .build();
            rdsDataClient.commitTransaction(commitRequest);
        } catch (Exception e) {
            if (transactionId != null) {
                RollbackTransactionRequest rollbackRequest = RollbackTransactionRequest.builder()
                        .resourceArn(databaseConfig.getDbClusterArn())
                        .secretArn(databaseConfig.getDbSecretArn())
                        .transactionId(transactionId)
                        .build();
            }

            throw new RuntimeException("Transactional operation failed. Rolling back.", e);
        }
    }
}
