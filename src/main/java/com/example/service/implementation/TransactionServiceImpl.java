package com.example.service.implementation;

import com.example.exception.ResourceNotFoundException;
import com.example.model.Transaction;
import com.example.model.request.TransactionRequest;
import com.example.repository.contract.TransactionRepository;
import com.example.service.contract.TransactionService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<Transaction> findAllForUserInDateRange(int userId, String startDate, String endDate) {
        return transactionRepository.findAllForUserInDateRange(userId, startDate, endDate);
    }

    @Override
    public Optional<Transaction> getTransactionById(int transactionId, int userId) {
        return transactionRepository.findByIdAndUserId(transactionId, userId);
    }

    @Override
    public void deleteByIdAndUserId(int transactionId, int userId) {
        transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + transactionId + " and userId: " + userId));

        transactionRepository.deleteByIdAndUserId(transactionId, userId);
    }

    @Override
    public void updateTransaction(int transactionId, int userId, TransactionRequest transactionDetails) {
        transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + transactionId + " and userId: " + userId));

        transactionRepository.update(transactionId, userId, transactionDetails);
    }
}
