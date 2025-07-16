package com.example.repository.implementation;

import com.example.model.Category;
import com.example.repository.AbstractJdbcRepository;
import com.example.repository.contract.CategoryRepository;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class CategoryRepositoryImpl extends AbstractJdbcRepository implements CategoryRepository {

    public CategoryRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Tworzy nową kategorię w bazie danych.
     * Zgodnie z kontraktem, nie zwraca nowo utworzonego obiektu.
     */
    @Override
    public void create(String name, int userId) {
        String sql = "INSERT INTO categories (name, user_id) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            // W prawdziwej aplikacji warto stworzyć bardziej specyficzny wyjątek
            throw new RuntimeException("Error creating category", e);
        }
    }

    @Override
    public List<Category> findAllByUserId(int userId) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name FROM categories WHERE user_id = ? ORDER BY name ASC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(mapRowToCategory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching categories for user " + userId, e);
        }
        return categories;
    }

    @Override
    public void update(int categoryId, String name, int userId) {
        String sql = "UPDATE categories SET name = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, categoryId);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating category" + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByIdAndUserId(int id, int userId) {
        String sql = "DELETE FROM categories WHERE id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category", e);
        }
    }

    @Override
    public Optional<Category> findByNameAndUserId(String name, int userId) {
        String sql = "SELECT id, name FROM categories WHERE name = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToCategory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching category by name", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Category> findByIdAndUserId(int categoryId, int userId) {
        String sql = "SELECT id, name FROM categories WHERE id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToCategory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching category by id", e);
        }
        return Optional.empty();
    }

    /**
     * Prywatna metoda pomocnicza do mapowania wiersza z ResultSet na obiekt Category.
     */
    private Category mapRowToCategory(ResultSet rs) throws SQLException {
        return new Category(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}