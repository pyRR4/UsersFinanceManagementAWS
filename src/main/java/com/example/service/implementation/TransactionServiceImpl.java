package com.example.service.implementation;

import com.example.model.Transaction;
import com.example.model.TransactionRequest;
import com.example.repository.contract.TransactionRepository;
import com.example.service.contract.TransactionService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public void createTransaction(TransactionRequest transaction, int userId) {
        transactionRepository.save(transaction, userId);
    }

    @Override
    public List<Transaction> getTransactionsForUser(int userId) {
        return transactionRepository.findAllByUserId(userId);
    }

    @Override
    public List<Transaction> getTransactionsForUserByCategory(int userId, int categoryId) {
        return transactionRepository.findAllByUserIdAndCategoryId(userId, categoryId);
    }
}
