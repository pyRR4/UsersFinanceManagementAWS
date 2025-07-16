package com.example.repository.implementation;

import com.example.model.SavingGoal;
import com.example.model.request.SavingGoalRequest;
import com.example.repository.AbstractJdbcRepository;
import com.example.repository.contract.SavingGoalRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SavingGoalRepositoryImpl extends AbstractJdbcRepository implements SavingGoalRepository {

    public SavingGoalRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public SavingGoal create(SavingGoalRequest request, int userId) {
        String sql = "INSERT INTO saving_goals (title, target_amount, user_id) " +
                "VALUES (?, ?, ?) " +
                "RETURNING id, title, target_amount, current_amount";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, request.getTitle());
            ps.setDouble(2, request.getTargetAmount());
            ps.setInt(3, userId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToSavingGoal(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating saving goal", e);
        }
        throw new IllegalStateException("Could not create saving goal and retrieve its data.");
    }

    @Override
    public List<SavingGoal> findAllByUserId(int userId) {
        List<SavingGoal> goals = new ArrayList<>();
        String sql = "SELECT id, title, target_amount, current_amount FROM saving_goals WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                goals.add(mapRowToSavingGoal(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching saving goals for user " + userId, e);
        }
        return goals;
    }

    @Override
    public Optional<SavingGoal> findByIdAndUserId(int goalId, int userId) {
        String sql = "SELECT id, title, target_amount, current_amount FROM saving_goals WHERE id = ? AND user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, goalId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToSavingGoal(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching saving goal by id", e);
        }
        return Optional.empty();
    }

    @Override
    public SavingGoal update(int goalId, int userId, SavingGoalRequest request) {
        String sql = "UPDATE saving_goals SET title = ?, target_amount = ? " +
                "WHERE id = ? AND user_id = ? " +
                "RETURNING id, title, target_amount, current_amount";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, request.getTitle());
            ps.setDouble(2, request.getTargetAmount());
            ps.setInt(3, goalId);
            ps.setInt(4, userId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToSavingGoal(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating saving goal", e);
        }
        throw new IllegalStateException("Could not update saving goal and retrieve its data.");
    }

    @Override
    public void deleteByIdAndUserId(int goalId, int userId) {
        String sql = "DELETE FROM saving_goals WHERE id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, goalId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting saving goal", e);
        }
    }

    @Override
    public double addFunds(int goalId, int userId, double amountToAdd) {
        try (Connection conn = dataSource.getConnection()) {
            return this.addFunds(goalId, userId, amountToAdd, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting connection for non-transactional addFunds", e);
        }
    }

    @Override
    public double addFunds(int goalId, int userId, double amountToAdd, Connection conn) {
        String sql = "UPDATE saving_goals SET current_amount = current_amount + ? " +
                "WHERE id = ? AND user_id = ? " +
                "RETURNING current_amount";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, amountToAdd);
            ps.setInt(2, goalId);
            ps.setInt(3, userId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("current_amount");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding funds to saving goal within a transaction", e);
        }
        throw new IllegalStateException("Could not add funds to saving goal.");
    }

    private SavingGoal mapRowToSavingGoal(ResultSet rs) throws SQLException {
        return new SavingGoal(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getDouble("target_amount"),
                rs.getDouble("current_amount")
        );
    }
}