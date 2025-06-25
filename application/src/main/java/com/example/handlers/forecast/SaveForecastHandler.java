package com.example.handlers.forecast;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.factory.DependencyFactory;
import com.example.model.Forecast;
import com.example.model.request.ForecastRequest;
import com.example.service.contract.ForecastService;

public class SaveForecastHandler implements RequestHandler<ForecastRequest, Forecast> {

    private final ForecastService forecastService;

    public SaveForecastHandler() {
        this.forecastService = DependencyFactory.getInstance().getService(ForecastService.class);
    }

    @Override
    public Forecast handleRequest(ForecastRequest input, Context context) {
        context.getLogger().log("Saving forecast for user: " + input.getUserId() +
                " for date: " + input.getForecastForDate());

        Forecast savedForecast = forecastService.saveForecast(input);

        context.getLogger().log("Forecast saved with ID: " + savedForecast.getId());
        return savedForecast;
    }
}
