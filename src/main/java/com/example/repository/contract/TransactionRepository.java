package com.example.repository.contract;

import com.example.model.Transaction;
import com.example.model.TransactionRequest;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    void save(TransactionRequest transaction, int userId);

    List<Transaction> findAllByUserId(int userId);
    List<Transaction> findAllByUserIdAndCategoryId(int userId, int categoryId);

    Optional<Transaction> findByIdAndUserId(int transactionId, int userId);

    void deleteByIdAndUserId(int transactionId, int userId);

    void update(int transactionId, int userId, TransactionRequest transactionDetails);
}
