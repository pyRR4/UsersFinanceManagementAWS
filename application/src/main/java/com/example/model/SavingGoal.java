package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SavingGoal {
    private final int id;
    private final String title;
    private final double targetAmount;
    private final double currentAmount;
}
