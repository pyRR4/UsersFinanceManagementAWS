package com.example.repository.implementation;

import com.example.config.DatabaseConfig;
import com.example.model.Transaction;
import com.example.model.TransactionRequest;
import com.example.repository.AbstractRdsRepository;
import com.example.repository.contract.TransactionRepository;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.util.ArrayList;
import java.util.List;

public class TransactionRepositoryImpl extends AbstractRdsRepository<Transaction> implements TransactionRepository {

    public TransactionRepositoryImpl(RdsDataClient rdsDataClient, DatabaseConfig dbConfig) {
        super(rdsDataClient, dbConfig);
    }

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

    @Override
    public List<Transaction> findAllByUserId(int userId) {
        String sql = "SELECT id, amount, date, description, category_id " +
                "FROM transactions " +
                "WHERE user_id = :user_id O" +
                "RDER BY date DESC";
        SqlParameter param = SqlParameter.builder().name("user_id").value(Field.builder().longValue((long) userId).build()).build();

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, param);

        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response);
    }

    @Override
    public List<Transaction> findAllByUserIdAndCategoryId(int userId, int categoryId) {
        String sql = "SELECT id, amount, date, description, category_id FROM transactions " +
                "WHERE user_id = :user_id AND category_id = :category_id ORDER BY date DESC";

        SqlParameter userParam = SqlParameter.builder().name("user_id").value(Field.builder().longValue((long) userId).build()).build();
        SqlParameter categoryParam = SqlParameter.builder().name("category_id").value(Field.builder().longValue((long) categoryId).build()).build();

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, userParam, categoryParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response);
    }

    @Override
    protected Transaction mapToEntity(List<Field> record) {
        return new Transaction(
                record.get(0).longValue().intValue(),                           // id
                record.get(1).doubleValue(),                                    // amount
                record.get(2).stringValue(),                                    // date
                record.get(3).isNull() ? null : record.get(3).stringValue(),    // description
                record.get(4).longValue().intValue()                            // category_id
        );
    }
}