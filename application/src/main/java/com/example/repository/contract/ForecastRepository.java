package com.example.repository.contract;

import com.example.model.Forecast;
import com.example.model.request.ForecastRequest;

public interface ForecastRepository {
    Forecast save(ForecastRequest forecast);
}
