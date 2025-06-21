package com.example.repository.implementation;

import com.example.config.DatabaseConfig;
import com.example.exception.ResourceNotFoundException;
import com.example.model.User;
import com.example.repository.AbstractRdsRepository;
import com.example.repository.contract.UserRepository;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;


public class UserRepositoryImpl extends AbstractRdsRepository<User> implements UserRepository {

    public UserRepositoryImpl(RdsDataClient rdsDataClient, DatabaseConfig databaseConfig) {
        super(rdsDataClient, databaseConfig);
    }

    @Override
    public Optional<User> findByCognitoSub(String cognitoSub) {
        String sql = "SELECT id, cognito_sub, email, balance, created_at FROM users WHERE cognito_sub = :cognito_sub";
        SqlParameter param = subParam(cognitoSub);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, param);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response).stream().findFirst();
    }

    @Override
    public User create(String cognitoSub, String email) {
        String sql = "INSERT INTO users (cognito_sub, email) VALUES (:cognito_sub, :email) RETURNING id, cognito_sub, email, balance, created_at";

        SqlParameter subParam = subParam(cognitoSub);
        SqlParameter emailParam = emailParam(email);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, subParam, emailParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response).get(0);
    }

    @Override
    public double updateBalance(int userId, double balanceDelta, String transactionId) {
        String sql = "UPDATE users SET balance = balance + :balance_delta " +
                "WHERE id = :user_id " +
                "RETURNING balance";

        SqlParameter idParam = idParam(userId);
        SqlParameter balanceDeltaParam = balanceDeltaParam(balanceDelta);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, transactionId, idParam, balanceDeltaParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        if(response.records() == null || response.records().isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + userId + ", cannot update balance.");
        }

        return response.records().get(0).get(0).doubleValue();
    }

    @Override
    public double getBalance(int userId) {
        String sql = "SELECT balance FROM users WHERE id = :user_id";

        SqlParameter idParam = idParam(userId);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, idParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        if(response.records() == null || response.records().isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + userId + ", cannot get balance.");
        }

        return response.records().get(0).get(0).doubleValue();
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, cognito_sub, email, balance, created_at FROM users";

        ExecuteStatementRequest request = createExecuteStatementRequest(sql);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response);
    }

    @Override
    protected User mapToEntity(List<Field> record) {
        return new User(
                record.get(0).longValue().intValue(),                                                           // id
                record.get(1).stringValue(),                                                                    // cognito_sub
                record.get(2).stringValue(),                                                                    // email
                BigDecimal.valueOf(record.get(3).doubleValue()),                                                // balance
                OffsetDateTime.parse(record.get(4).stringValue().replace(" ", "T") + "Z") // created_at
        );
    }

    private SqlParameter subParam(String cognitoSub) {
        return SqlParameter.builder()
                .name("cognito_sub")
                .value(Field.builder().stringValue(cognitoSub).build())
                .build();
    }

    private SqlParameter emailParam(String email) {
        return SqlParameter.builder()
                .name("email")
                .value(Field.builder().stringValue(email).build())
                .build();
    }

    private SqlParameter idParam(int id) {
        return SqlParameter.builder()
                .name("user_id")
                .value(Field.builder().longValue((long) id).build())
                .build();
    }

    private SqlParameter balanceDeltaParam(double balanceDelta) {
        return SqlParameter.builder()
                .name("balance_delta")
                .value(Field.builder().doubleValue(balanceDelta).build())
                .build();
    }
}
