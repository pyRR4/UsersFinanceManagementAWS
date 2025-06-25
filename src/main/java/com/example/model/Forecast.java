package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;

@AllArgsConstructor
@Getter
@ToString
public class Forecast {
    private int id;
    private int userId;
    private String forecastForDate;
    private double forecastedAmount;
    private String algorithmVersion;
    private OffsetDateTime createdAt;
}
