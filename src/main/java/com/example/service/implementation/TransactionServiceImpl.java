package com.example.service.implementation;

import com.example.model.TransactionRequest;
import com.example.repository.contract.TransactionRepository;
import com.example.service.contract.TransactionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public void createTransaction(TransactionRequest transaction, int userId) {
        transactionRepository.save(transaction, userId);
    }
}
