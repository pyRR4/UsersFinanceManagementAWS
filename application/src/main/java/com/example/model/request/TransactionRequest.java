package com.example.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TransactionRequest {
    private double amount;
    private String categoryName;
    private String description;
    private String date;            //DATA W PEŁNYM FORMACIE ISO 8601
}
