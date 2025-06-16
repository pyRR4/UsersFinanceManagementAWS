package com.example.service.contract;

import com.example.model.Transaction;
import com.example.model.TransactionRequest;

import java.util.List;

public interface TransactionService {
    void createTransaction(TransactionRequest transaction, int userId);
    List<Transaction> getTransactionsForUser(int userId);
    List<Transaction> getTransactionsForUserByCategory(int userId, int categoryId);
}
