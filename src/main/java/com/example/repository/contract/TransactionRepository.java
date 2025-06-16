package com.example.repository.contract;

import com.example.model.TransactionRequest;

public interface TransactionRepository {
    void save(TransactionRequest transaction, int userId);
}
