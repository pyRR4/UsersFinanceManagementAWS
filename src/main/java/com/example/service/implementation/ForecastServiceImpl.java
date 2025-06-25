package com.example.service.implementation;

import com.example.model.Forecast;
import com.example.model.Transaction;
import com.example.model.request.ForecastRequest;
import com.example.repository.contract.ForecastRepository;
import com.example.service.contract.ForecastService;
import com.example.strategy.ForecastingStrategy;

import java.util.List;

public class ForecastServiceImpl implements ForecastService {

    private final ForecastRepository forecastRepository;

    public ForecastServiceImpl(ForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }

    @Override
    public double calculateExpenseForecast(List<Transaction> transactions, ForecastingStrategy strategy) {
        return strategy.calculate(transactions);
    }

    @Override
    public Forecast saveForecast(ForecastRequest forecastRequest) {
        return forecastRepository.save(forecastRequest);
    }
}
