package com.example.repository.implementation;

import com.example.config.DatabaseConfig;
import com.example.model.Category;
import com.example.repository.AbstractRdsRepository;
import com.example.repository.contract.CategoryRepository;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.util.List;
import java.util.Optional;

public class CategoryRepositoryImpl extends AbstractRdsRepository<Category> implements CategoryRepository {

    public CategoryRepositoryImpl(RdsDataClient rdsDataClient, DatabaseConfig dbConfig) {
        super(rdsDataClient, dbConfig);
    }

    @Override
    public void create(String name, int userId) {
        String sql = "INSERT INTO categories (name, user_id) VALUES (:name, :user_id)";

        SqlParameter nameParam = nameParam(name);
        SqlParameter userParam = userParam(userId);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, nameParam, userParam);
        rdsDataClient.executeStatement(request);
    }

    @Override
    public List<Category> findAllByUserId(int userId) {
        String sql = "SELECT id, name FROM categories WHERE user_id = :user_id ORDER BY name ASC";
        SqlParameter userParam = userParam(userId);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, userParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response);
    }

    @Override
    public void update(int categoryId, String name, int userId) {
        String sql = "UPDATE categories SET name = :name WHERE id = :category_id AND user_id = :user_id";

        SqlParameter nameParam = nameParam(name);
        SqlParameter categoryParam = idParam(categoryId);
        SqlParameter userParam = userParam(userId);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, nameParam, categoryParam, userParam);
        rdsDataClient.executeStatement(request);
    }

    @Override
    public void deleteByIdAndUserId(int id, int userId) {
        String sql = "DELETE FROM categories WHERE id = :id AND user_id = :user_id";

        SqlParameter idParam = idParam(id);
        SqlParameter userParam = userParam(userId);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, idParam, userParam);
        rdsDataClient.executeStatement(request);
    }

    @Override
    public Optional<Category> findByNameAndUserId(String name, int userId) {
        String sql = "SELECT id, name FROM categories WHERE name = :name AND user_id = :user_id";

        SqlParameter nameParam = nameParam(name);
        SqlParameter userParam = userParam(userId);

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, nameParam, userParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response).stream().findFirst();
    }

    @Override
    public Optional<Category> findByIdAndUserId(int categoryId, int userId) {
        String sql = "SELECT id, name FROM categories WHERE id = :category_id AND user_id = :user_id";

        SqlParameter categoryParam = SqlParameter.builder().name("category_id").value(Field.builder().longValue((long) categoryId).build()).build();
        SqlParameter userParam = SqlParameter.builder().name("user_id").value(Field.builder().longValue((long) userId).build()).build();

        ExecuteStatementRequest request = createExecuteStatementRequest(sql, categoryParam, userParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(request);

        return mapResponseToList(response).stream().findFirst();
    }

    @Override
    protected Category mapToEntity(List<Field> record) {
        return new Category(
                record.get(0).longValue().intValue(),   // id
                record.get(1).stringValue()             // name
        );
    }

    private SqlParameter idParam(int id) {
        return SqlParameter.builder()
                .name("id")
                .value(Field.builder().longValue((long) id).build())
                .build();
    }

    private SqlParameter nameParam(String name) {
        return SqlParameter.builder()
                .name("name")
                .value(Field.builder().stringValue(name).build())
                .build();
    }

    private SqlParameter userParam(int userId) {
        return SqlParameter.builder()
                .name("user_id")
                .value(Field.builder().longValue((long) userId).build())
                .build();
    }
}
