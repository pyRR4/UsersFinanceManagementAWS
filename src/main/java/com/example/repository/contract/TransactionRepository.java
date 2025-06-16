package com.example.repository.contract;

import com.example.model.Transaction;
import com.example.model.TransactionRequest;

import java.util.List;

public interface TransactionRepository {
    void save(TransactionRequest transaction, int userId);
    List<Transaction> findAllByUserId(int userId);
    List<Transaction> findAllByUserIdAndCategoryId(int userId, int categoryId);
}
