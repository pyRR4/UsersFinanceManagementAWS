package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@ToString
public class User {
    private final int id;
    private final String cognitoSub;
    private final String email;
    private final BigDecimal balance;
    private final OffsetDateTime createdAt;
}