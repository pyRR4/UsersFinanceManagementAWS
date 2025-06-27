package com.example.service.implementation;

import com.example.exception.InsufficientFundsException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.SavingGoal;
import com.example.model.request.SavingGoalRequest;
import com.example.repository.contract.SavingGoalRepository;
import com.example.service.contract.TransactionManager;
import com.example.repository.contract.TransactionRepository;
import com.example.repository.contract.UserRepository;
import com.example.service.contract.SavingGoalService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SavingGoalServiceImpl implements SavingGoalService {

    private final SavingGoalRepository savingGoalRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionManager transactionManager;

    @Override
    public SavingGoal createGoal(SavingGoalRequest request, int userId) {
        return savingGoalRepository.create(request, userId);
    }

    @Override
    public List<SavingGoal> getGoalsForUser(int userId) {
        return savingGoalRepository.findAllByUserId(userId);
    }

    @Override
    public SavingGoal updateGoal(SavingGoalRequest request, int userId, int goalId) {
        savingGoalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Saving goal not found with id: " + goalId));

        return savingGoalRepository.update(goalId, userId, request);
    }

    @Override
    public void deleteGoal(int goalId, int userId) {
        transactionManager.execute(transactionId -> {
            SavingGoal goalToDelete = savingGoalRepository.findByIdAndUserId(goalId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Saving goal not found with id: " + goalId));

            if (goalToDelete.getCurrentAmount() > 0) {
                userRepository.updateBalance(userId, goalToDelete.getCurrentAmount());
            }

            savingGoalRepository.deleteByIdAndUserId(goalId, userId);
        });
    }

    @Override
    public void addFundsToGoal(int goalId, int userId, double amountToAdd) {
        if (amountToAdd <= 0) {
            throw new IllegalArgumentException("Amount to add must be positive.");
        }

        savingGoalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Saving goal not found with id: " + goalId));

        double currentBalance = userRepository.getBalance(userId);
        if (currentBalance < amountToAdd) {
            throw new InsufficientFundsException("Insufficient funds. Current balance: " + currentBalance);
        }

        transactionManager.execute(transactionId -> {
            userRepository.updateBalance(userId, -amountToAdd);
            savingGoalRepository.addFunds(goalId, userId, amountToAdd);
        });
    }
}
