package com.example.repository.implementation;

import com.example.config.DatabaseConfig;
import com.example.model.User;
import com.example.repository.AbstractRdsRepository;
import com.example.repository.contract.UserRepository;
import lombok.RequiredArgsConstructor;
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
        SqlParameter param = SqlParameter.builder().name("cognito_sub").value(Field.builder().stringValue(cognitoSub).build()).build();

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, param);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response).stream().findFirst();
    }

    @Override
    public User create(String cognitoSub, String email) {
        String sql = "INSERT INTO users (cognito_sub, email) VALUES (:cognito_sub, :email) RETURNING id, cognito_sub, email, balance, created_at";

        SqlParameter subParam = SqlParameter.builder().name("cognito_sub").value(Field.builder().stringValue(cognitoSub).build()).build();
        SqlParameter emailParam = SqlParameter.builder().name("email").value(Field.builder().stringValue(email).build()).build();

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, subParam, emailParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response).get(0);
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
}
