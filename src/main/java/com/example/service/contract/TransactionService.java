package com.example.service.contract;

import com.example.model.TransactionRequest;

public interface TransactionService {
    void createTransaction(TransactionRequest transaction, int userId);
}
