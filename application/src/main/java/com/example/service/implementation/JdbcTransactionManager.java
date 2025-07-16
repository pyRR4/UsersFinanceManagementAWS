package com.example.service.implementation;

import com.example.config.DatabaseConfig;
import com.example.service.contract.TransactionManager;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.BeginTransactionRequest;
import software.amazon.awssdk.services.rdsdata.model.BeginTransactionResponse;
import software.amazon.awssdk.services.rdsdata.model.CommitTransactionRequest;
import software.amazon.awssdk.services.rdsdata.model.RollbackTransactionRequest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class JdbcTransactionManager implements TransactionManager {

    private final DataSource dataSource;

    @Override
    public void execute(Consumer<Connection> transactionalLogic) {
        Connection conn = null;
        try {
            // 1. Pobieramy jedno połączenie z puli
            conn = dataSource.getConnection();
            // 2. Wyłączamy auto-commit, rozpoczynając transakcję
            conn.setAutoCommit(false);

            // 3. Wykonujemy logikę biznesową, przekazując jej nasze połączenie
            transactionalLogic.accept(conn);

            // 4. Jeśli logika nie rzuciła wyjątku, zatwierdzamy transakcję
            conn.commit();

        } catch (Exception e) {
            // 5. Jeśli wystąpił jakikolwiek błąd, wycofujemy transakcję
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Failed to rollback transaction", ex);
                }
            }
            // Rzucamy wyjątek dalej, aby handler go złapał
            throw new RuntimeException("Transactional operation failed", e);
        } finally {
            // 6. Niezależnie od wyniku, zamykamy połączenie (zwracamy je do puli)
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Dobre praktyki
                    conn.close();
                } catch (SQLException e) {
                    // Logujemy błąd, ale nie rzucamy go dalej
                    e.printStackTrace();
                }
            }
        }
    }
}
