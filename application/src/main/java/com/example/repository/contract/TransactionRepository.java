package com.example.repository.contract;

import com.example.model.Transaction;
import com.example.model.request.TransactionRequest;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    void save(TransactionRequest transaction, int userId);
    void save(TransactionRequest transaction, int userId, Connection conn);
    List<Transaction> findAllByUserId(int userId);
    List<Transaction> findAllByUserIdAndCategoryId(int userId, int categoryId);
    List<Transaction> findAllForUserInDateRange(int userId, String startDate, String endDate);
    Optional<Transaction> findByIdAndUserId(int transactionId, int userId);
    void deleteByIdAndUserId(int transactionId, int userId);
    void update(int transactionId, int userId, TransactionRequest transactionDetails);
}