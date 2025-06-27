package com.example.repository.implementation;

import com.example.exception.ResourceNotFoundException;
import com.example.model.User;
import com.example.repository.AbstractJdbcRepository;
import com.example.repository.contract.UserRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl extends AbstractJdbcRepository implements UserRepository {

    public UserRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<User> findByCognitoSub(String cognitoSub) {
        String sql = "SELECT id, cognito_sub, email, balance, created_at FROM users WHERE cognito_sub = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cognitoSub);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by cognito sub", e);
        }
        return Optional.empty();
    }

    @Override
    public User create(String cognitoSub, String email) {
        String sql = "INSERT INTO users (cognito_sub, email) VALUES (?, ?) RETURNING id, cognito_sub, email, balance, created_at";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cognitoSub);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }
        throw new IllegalStateException("Could not create user and retrieve its data.");
    }

    @Override
    public double updateBalance(int userId, double balanceDelta) {
        String sql = "UPDATE users SET balance = balance + ? WHERE id = ? RETURNING balance";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, balanceDelta);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user balance", e);
        }
        throw new ResourceNotFoundException("User not found with id: " + userId + ", cannot update balance.");
    }

    @Override
    public double getBalance(int userId) {
        String sql = "SELECT balance FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user balance", e);
        }
        throw new ResourceNotFoundException("User not found with id: " + userId);
    }

    @Override
    public List<User> getAllActiveUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, cognito_sub, email, balance, created_at FROM users ORDER BY id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all users", e);
        }
        return users;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("cognito_sub"),
                rs.getString("email"),
                rs.getBigDecimal("balance"),
                rs.getObject("created_at", OffsetDateTime.class)
        );
    }
}