package com.example.repository.implementation;

import com.example.config.DatabaseConfig;
import com.example.model.Transaction;
import com.example.model.request.TransactionRequest;
import com.example.repository.AbstractRdsRepository;
import com.example.repository.contract.TransactionRepository;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionRepositoryImpl extends AbstractRdsRepository<Transaction> implements TransactionRepository {

    public TransactionRepositoryImpl(RdsDataClient rdsDataClient, DatabaseConfig dbConfig) {
        super(rdsDataClient, dbConfig);
    }

    @Override
    public void save(TransactionRequest transaction, int userId) {
        String sql = "INSERT INTO transactions (user_id, amount, date, description, category_id) " +
                "VALUES (:user_id, :amount, :date, :description, :category_id)";

        List<SqlParameter> parameters = new ArrayList<>();
        parameters.add(userIdParam(userId));
        parameters.add(amountParam(transaction.getAmount()));
        parameters.add(dateParam(transaction.getDate()));
        parameters.add(descParam(transaction.getDescription()));
        parameters.add(categoryIdParam(transaction.getCategoryId()));

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
        SqlParameter param = userIdParam(userId);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, param);

        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response);
    }

    @Override
    public List<Transaction> findAllByUserIdAndCategoryId(int userId, int categoryId) {
        String sql = "SELECT id, amount, date, description, category_id FROM transactions " +
                "WHERE user_id = :user_id AND category_id = :category_id ORDER BY date DESC";

        SqlParameter userParam = userIdParam(userId);
        SqlParameter categoryParam = categoryIdParam(categoryId);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, userParam, categoryParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response);
    }

    @Override
    public Optional<Transaction> findByIdAndUserId(int transactionId, int userId) {
        String sql = "SELECT id, amount, date, description, category_id FROM transactions WHERE id = :transaction_id AND user_id = :user_id";
        SqlParameter idParam = transactionIdParam(transactionId);
        SqlParameter userParam = userIdParam(userId);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, idParam, userParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response).stream().findFirst();
    }

    @Override
    public void deleteByIdAndUserId(int transactionId, int userId) {
        String sql = "DELETE FROM transactions WHERE id = :transaction_id AND user_id = :user_id";
        SqlParameter idParam = transactionIdParam(transactionId);
        SqlParameter userParam = userIdParam(userId);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, idParam, userParam);
        rdsDataClient.executeStatement(request);
    }

    @Override
    public void update(int transactionId, int userId, TransactionRequest transactionDetails) {
        String sql = "UPDATE transactions SET amount = :amount, category_id = :category_id, " +
                "description = :description, date = :date " +
                "WHERE id = :transaction_id AND user_id = :user_id";

        SqlParameter amountParam = amountParam(transactionDetails.getAmount());
        SqlParameter categoryParam = categoryIdParam(transactionDetails.getCategoryId());
        SqlParameter descriptionParam = descParam(transactionDetails.getDescription());
        SqlParameter dateParam = dateParam(transactionDetails.getDate());
        SqlParameter idParam = transactionIdParam(transactionId);
        SqlParameter userParam = userIdParam(userId);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, amountParam,
                categoryParam, descriptionParam, dateParam, idParam, userParam);
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

    private SqlParameter transactionIdParam(int transactionId) {
        return SqlParameter.builder()
                .name("transaction_id")
                .value(Field.builder().longValue((long)transactionId).build())
                .build();
    }

    private SqlParameter userIdParam(int userId) {
        return SqlParameter.builder()
                .name("user_id")
                .value(Field.builder().longValue((long)userId).build())
                .build();
    }

    private SqlParameter categoryIdParam(int categoryId) {
        return SqlParameter.builder()
                .name("category_id")
                .value(Field.builder().longValue((long)categoryId).build())
                .build();
    }

    private SqlParameter amountParam(double amount) {
        return SqlParameter.builder()
                .name("amount")
                .value(Field.builder().doubleValue(amount).build())
                .build();
    }

    private SqlParameter descParam(String description) {
        return SqlParameter.builder()
                .name("description")
                .value(Field.builder().stringValue(description).build())
                .build();
    }

    private SqlParameter dateParam(String date) {
        return SqlParameter.builder()
                .name("date")
                .value(Field.builder().stringValue(date).build())
                .build();
    }
}