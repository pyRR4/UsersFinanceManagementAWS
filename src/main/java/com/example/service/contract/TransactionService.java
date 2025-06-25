package com.example.service.contract;

import com.example.model.Transaction;
import com.example.model.request.TransactionRequest;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    void createTransaction(TransactionRequest transaction, int userId);
    List<Transaction> getTransactionsForUser(int userId);
    List<Transaction> getTransactionsForUserByCategory(int userId, int categoryId);
    List<Transaction> findAllForUserInDateRange(int userId, String startDate, String endDate);
    Optional<Transaction> getTransactionById(int transactionId, int userId);
    void deleteByIdAndUserId(int transactionId, int userId);
    void updateTransaction(int transactionId, int userId, TransactionRequest transactionDetails);
}
