package com.example.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastRequest {
    private int userId;
    private LocalDate forecastForDate;
    private double forecastedAmount;
    private String algorithmVersion;
}
