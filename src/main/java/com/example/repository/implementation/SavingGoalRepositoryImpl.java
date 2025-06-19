package com.example.repository.implementation;

import com.example.config.DatabaseConfig;
import com.example.model.SavingGoal;
import com.example.model.request.SavingGoalRequest;
import com.example.repository.AbstractRdsRepository;
import com.example.repository.contract.SavingGoalRepository;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.util.List;
import java.util.Optional;

public class SavingGoalRepositoryImpl extends AbstractRdsRepository<SavingGoal> implements SavingGoalRepository {

    public SavingGoalRepositoryImpl(RdsDataClient rdsDataClient, DatabaseConfig dbConfig) {
        super(rdsDataClient, dbConfig);
    }

    @Override
    public SavingGoal create(SavingGoalRequest request, int userId) {
        String sql = "INSERT INTO saving_goals (title, target_amount, user_id) " +
                "VALUES (:title, :target_amount, :user_id) " +
                "RETURNING id, title, target_amount, current_amount";

        SqlParameter titleParam = titleParam(request.getTitle());
        SqlParameter targetParam = targetAmountParam(request.getTargetAmount());
        SqlParameter userParam = userParam(userId);

        ExecuteStatementRequest sqlRequest = createExecuteStatementRequest(sql, titleParam, targetParam, userParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(sqlRequest);

        return mapResponseToList(response).get(0);
    }

    @Override
    public List<SavingGoal> findAllByUserId(int userId) {
        String sql = "SELECT id, title, target_amount, current_amount FROM saving_goals WHERE user_id = :user_id";
        SqlParameter userParam = userParam(userId);

        ExecuteStatementRequest sqlRequest = createExecuteStatementRequest(sql, userParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(sqlRequest);

        return mapResponseToList(response);
    }

    @Override
    public Optional<SavingGoal> findByIdAndUserId(int goalId, int userId) {
        String sql = "SELECT id, title, target_amount, current_amount FROM saving_goals WHERE id = :goal_id AND user_id = :user_id";
        SqlParameter goalParam = goalParam(goalId);
        SqlParameter userParam = userParam(userId);

        ExecuteStatementRequest sqlRequest = createExecuteStatementRequest(sql, goalParam, userParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(sqlRequest);

        return mapResponseToList(response).stream().findFirst();
    }

    @Override
    public SavingGoal update(int goalId, int userId, SavingGoalRequest request) {
        String sql = "UPDATE saving_goals SET title = :title, target_amount = :target_amount " +
                "WHERE id = :goal_id AND user_id = :user_id " +
                "RETURNING id, title, target_amount, current_amount";

        SqlParameter titleParam = titleParam(request.getTitle());
        SqlParameter targetParam = targetAmountParam(request.getTargetAmount());
        SqlParameter goalParam = goalParam(goalId);
        SqlParameter userParam = userParam(userId);

        ExecuteStatementRequest sqlRequest = createExecuteStatementRequest(sql, titleParam, targetParam, goalParam, userParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(sqlRequest);

        return mapResponseToList(response).get(0);
    }

    @Override
    public void deleteByIdAndUserId(int goalId, int userId) {
        deleteByIdAndUserId(goalId, userId, null);
    }

    @Override
    public void deleteByIdAndUserId(int goalId, int userId, String transactionId) {
        String sql = "DELETE FROM saving_goals WHERE id = :goal_id AND user_id = :user_id";
        SqlParameter goalParam = goalParam(goalId);
        SqlParameter userParam = userParam(userId);

        ExecuteStatementRequest sqlRequest = createExecuteStatementRequest(sql, transactionId, goalParam, userParam);
        rdsDataClient.executeStatement(sqlRequest);
    }

    @Override
    public double addFunds(int goalId, int userId, double amountToAdd) {
        return this.addFunds(goalId, userId, amountToAdd, null);
    }

    @Override
    public double addFunds(int goalId, int userId, double amountToAdd, String transactionId) {
        String sql = "UPDATE saving_goals SET current_amount = current_amount + :amount_to_add " +
                "WHERE id = :goal_id AND user_id = :user_id " +
                "RETURNING current_amount";

        SqlParameter amountParam = amountToAddParam(amountToAdd);
        SqlParameter goalParam = goalParam(goalId);
        SqlParameter userParam = userParam(userId);

        ExecuteStatementRequest sqlRequest = createExecuteStatementRequest(sql, transactionId, amountParam, goalParam, userParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(sqlRequest);

        return response.records().get(0).get(0).doubleValue();
    }

    @Override
    protected SavingGoal mapToEntity(List<Field> record) {
        return new SavingGoal(
                record.get(0).longValue().intValue(), // id
                record.get(1).stringValue(),          // title
                record.get(2).doubleValue(),          // target_amount
                record.get(3).doubleValue()           // current_amount
        );
    }

    private SqlParameter titleParam(String title) {
        return SqlParameter.builder()
                .name("title")
                .value(Field.builder().stringValue(title).build())
                .build();
    }

    private SqlParameter targetAmountParam(double targetAmount) {
        return SqlParameter.builder()
                .name("target_amount")
                .value(Field.builder().doubleValue(targetAmount).build())
                .build();
    }

    private SqlParameter userParam(int userId) {
        return SqlParameter.builder()
                .name("user_id")
                .value(Field.builder().longValue((long)userId).build())
                .build();
    }

    private SqlParameter goalParam(int goalId) {
        return SqlParameter.builder()
                .name("goal_id")
                .value(Field.builder().longValue((long)goalId).build())
                .build();
    }

    private SqlParameter amountToAddParam(double amount) {
        return SqlParameter.builder()
                .name("amount_to_add")
                .value(Field.builder().doubleValue(amount).build())
                .build();
    }


}
