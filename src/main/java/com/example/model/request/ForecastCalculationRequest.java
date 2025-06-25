package com.example.model.request;

import com.example.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastCalculationRequest {
    private int userId;
    private List<Transaction> transactions;
}
