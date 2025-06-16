package com.example.model;

import lombok.Getter;

@Getter
public class TransactionRequest {

    private double amount;
    private int categoryId;
    private String description;
    private String date;
}
