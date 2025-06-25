package com.example.repository.contract;

import com.example.model.SavingGoal;
import com.example.model.request.SavingGoalRequest;

import java.util.List;
import java.util.Optional;

public interface SavingGoalRepository {
    SavingGoal create(SavingGoalRequest request, int userId);
    List<SavingGoal> findAllByUserId(int userId);
    Optional<SavingGoal> findByIdAndUserId(int goalId, int userId);
    SavingGoal update(int goalId, int userId, SavingGoalRequest request);
    void deleteByIdAndUserId(int goalId, int userId);
    void deleteByIdAndUserId(int goalId, int userId, String transactionId);
    double addFunds(int goalId, int userId, double amountToAdd);
    double addFunds(int goalId, int userId, double amountToAdd, String transactionId);
}
