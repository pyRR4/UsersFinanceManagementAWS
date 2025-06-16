package com.example.model.request;

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
