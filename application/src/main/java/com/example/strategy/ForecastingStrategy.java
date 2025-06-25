package com.example.strategy;

import com.example.model.Transaction;

import java.util.List;

public interface ForecastingStrategy {
    double calculate(List<Transaction> transactions);
    String getAlgorithmName();
}
