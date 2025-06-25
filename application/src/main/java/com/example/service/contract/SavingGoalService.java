package com.example.service.contract;

import com.example.model.SavingGoal;
import com.example.model.request.SavingGoalRequest;

import java.util.List;

public interface SavingGoalService {
    SavingGoal createGoal(SavingGoalRequest request, int userId);
    List<SavingGoal> getGoalsForUser(int userId);
    SavingGoal updateGoal(SavingGoalRequest request, int userId, int goalId);
    void deleteGoal(int goalId, int userId);
    void addFundsToGoal(int goalId, int userId, double amountToAdd);
}
