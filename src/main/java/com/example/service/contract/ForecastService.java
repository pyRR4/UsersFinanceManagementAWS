package com.example.service.contract;

import com.example.model.Forecast;
import com.example.model.Transaction;
import com.example.model.request.ForecastRequest;
import com.example.strategy.ForecastingStrategy;

import java.util.List;

public interface ForecastService {
    double calculateExpenseForecast(List<Transaction> transactions, ForecastingStrategy strategy);
    Forecast saveForecast(ForecastRequest forecastRequest);
}
