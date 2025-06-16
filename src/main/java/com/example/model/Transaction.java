package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Transaction {
    private final int id;
    private final double amount;
    private final String date;
    private final String description;
    private final int categoryId;
}