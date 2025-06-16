package com.example.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TransactionRequest {
    private double amount;
    private int categoryId;
    private String description;
    private String date;
}
