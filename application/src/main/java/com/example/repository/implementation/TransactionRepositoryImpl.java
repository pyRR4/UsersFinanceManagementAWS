package com.example.repository.implementation;

import com.example.exception.ResourceNotFoundException;
import com.example.factory.DependencyFactory;
import com.example.model.Transaction;
import com.example.model.request.TransactionRequest;
import com.example.repository.AbstractJdbcRepository;
import com.example.repository.contract.CategoryRepository;
import com.example.repository.contract.TransactionRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionRepositoryImpl extends AbstractJdbcRepository implements TransactionRepository {

    private final CategoryRepository categoryRepository;

    public TransactionRepositoryImpl(DataSource dataSource, CategoryRepository categoryRepository) {
        super(dataSource);
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void save(TransactionRequest transaction, int userId) {
        try (Connection conn = dataSource.getConnection()) {
            this.save(transaction, userId, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting connection for non-transactional save", e);
        }
    }

    @Override
    public void save(TransactionRequest transaction, int userId, Connection conn) {
        String sql = "INSERT INTO transactions (user_id, amount, date, description, category_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            int categoryId = categoryRepository.findByNameAndUserId(transaction.getCategoryName(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category with name: " + transaction.getCategoryName()
                            + " not found for user with id: " + userId)).getId();

            ps.setInt(1, userId);
            ps.setDouble(2, transaction.getAmount());
            ps.setObject(3, OffsetDateTime.parse(transaction.getDate()));
            ps.setString(4, transaction.getDescription());
            ps.setInt(5, categoryId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving transaction within a transaction", e);
        }
    }

    @Override
    public List<Transaction> findAllByUserId(int userId) {
        String sql = "SELECT id, amount, date, description, category_id FROM transactions WHERE user_id = ? ORDER BY date DESC";
        return findManyByQuery(sql, userId);
    }

    @Override
    public List<Transaction> findAllByUserIdAndCategoryId(int userId, int categoryId) {
        String sql = "SELECT id, amount, date, description, category_id FROM transactions WHERE user_id = ? AND category_id = ? ORDER BY date DESC";
        return findManyByQuery(sql, userId, categoryId);
    }

    @Override
    public List<Transaction> findAllForUserInDateRange(int userId, String startDate, String endDate) {
        String sql = "SELECT id, amount, date, description, category_id FROM transactions " +
                "WHERE user_id = ? AND date >= ?::timestamptz AND date < ?::timestamptz ORDER BY date ASC";
        return findManyByQuery(sql, userId, startDate, endDate);
    }

    @Override
    public Optional<Transaction> findByIdAndUserId(int transactionId, int userId) {
        String sql = "SELECT id, amount, date, description, category_id FROM transactions WHERE id = ? AND user_id = ?";
        List<Transaction> results = findManyByQuery(sql, transactionId, userId);
        return results.stream().findFirst();
    }

    @Override
    public void deleteByIdAndUserId(int transactionId, int userId) {
        String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, transactionId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting transaction", e);
        }
    }

    @Override
    public void update(int transactionId, int userId, TransactionRequest details) {
        String sql = "UPDATE transactions SET amount = ?, category_id = ?, description = ?, date = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int categoryId = categoryRepository.findByNameAndUserId(details.getCategoryName(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category with name: " + details.getCategoryName()
                            + " not found for user with id: " + userId)).getId();

            ps.setDouble(1, details.getAmount());
            ps.setInt(2, categoryId);
            ps.setString(3, details.getDescription());
            ps.setObject(4, OffsetDateTime.parse(details.getDate()));
            ps.setInt(5, transactionId);
            ps.setInt(6, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating transaction", e);
        }
    }

    private List<Transaction> findManyByQuery(String sql, Object... params) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing transaction query", e);
        }
        return transactions;
    }

    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("id"),
                rs.getDouble("amount"),
                rs.getObject("date", OffsetDateTime.class).toString(),
                rs.getString("description"),
                rs.getInt("category_id")
        );
    }
}