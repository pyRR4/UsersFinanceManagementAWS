package com.example.handlers.forecast;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.factory.DependencyFactory;
import com.example.model.request.ForecastCalculationRequest;
import com.example.model.request.ForecastRequest;
import com.example.service.contract.ForecastService;
import com.example.strategy.ForecastingStrategy;
import com.example.strategy.SimpleAverageStrategy;

import java.time.LocalDate;

public class CalculateForecastHandler implements RequestHandler<ForecastCalculationRequest, ForecastRequest> {

    private final ForecastService forecastService;
    private final ForecastingStrategy defaultStrategy;

    public CalculateForecastHandler() {
        DependencyFactory factory = DependencyFactory.getInstance();
        this.forecastService = factory.getService(ForecastService.class);
        this.defaultStrategy = factory.getService(SimpleAverageStrategy.class);
    }

    @Override
    public ForecastRequest handleRequest(ForecastCalculationRequest input, Context context) {
        context.getLogger().log("Calculating forecast for user: " + input.getUserId());

        double forecastedAmount = forecastService.calculateExpenseForecast(input.getTransactions(), defaultStrategy);
        context.getLogger().log("Calculated forecasted amount: " + forecastedAmount);

        LocalDate forecastDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        String algorithmVersion = defaultStrategy.getAlgorithmName();

        return new ForecastRequest(
                input.getUserId(),
                forecastDate,
                forecastedAmount,
                algorithmVersion
        );
    }
}
