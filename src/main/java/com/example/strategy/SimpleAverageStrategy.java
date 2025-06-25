package com.example.strategy;

import com.example.model.Transaction;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleAverageStrategy implements ForecastingStrategy {
    private static final int MONTHS_OF_HISTORY = 3;

    @Override
    public double calculate(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return 0.0;
        }

        Map<YearMonth, Double> monthlyTotals = transactions.stream()
                .collect(Collectors.groupingBy(
                        tx -> YearMonth.from(OffsetDateTime.parse(tx.getDate())),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        YearMonth currentMonth = YearMonth.now();
        double total = 0;
        int monthsCount = 0;

        for (int i = 1; i <= MONTHS_OF_HISTORY; i++) {
            YearMonth pastMonth = currentMonth.minusMonths(i);
            if (monthlyTotals.containsKey(pastMonth)) {
                total += monthlyTotals.get(pastMonth);
                monthsCount++;
            }
        }

        return monthsCount > 0 ? total / monthsCount : 0.0;
    }

    @Override
    public String getAlgorithmName() {
        return "simple_moving_average_3m";
    }
}
