package com.example.repository.implementation;

import com.example.model.Forecast;
import com.example.model.request.ForecastRequest; // Upewnij się, że ścieżka do modelu jest poprawna
import com.example.repository.AbstractJdbcRepository;
import com.example.repository.contract.ForecastRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;

public class ForecastRepositoryImpl extends AbstractJdbcRepository implements ForecastRepository {

    public ForecastRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Forecast save(ForecastRequest request) {
        String sql = "INSERT INTO forecasts (user_id, forecast_for_date, forecasted_amount, algorithm_version) " +
                "VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (user_id, forecast_for_date) DO UPDATE SET " +
                "forecasted_amount = EXCLUDED.forecasted_amount, " +
                "algorithm_version = EXCLUDED.algorithm_version, " +
                "created_at = NOW() " +
                "RETURNING id, user_id, forecast_for_date, forecasted_amount, algorithm_version, created_at";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, request.getUserId());
            ps.setObject(2, request.getForecastForDate(), Types.DATE);
            ps.setDouble(3, request.getForecastedAmount());
            ps.setString(4, request.getAlgorithmVersion());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToForecast(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving forecast for user " + request.getUserId(), e);
        }
        throw new IllegalStateException("Could not save forecast and retrieve result.");
    }

    private Forecast mapRowToForecast(ResultSet rs) throws SQLException {
        return new Forecast(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getDate("forecast_for_date").toLocalDate().toString(), // Poprawne mapowanie z java.sql.Date
                rs.getDouble("forecasted_amount"),
                rs.getString("algorithm_version"),
                rs.getObject("created_at", OffsetDateTime.class)
        );
    }
}