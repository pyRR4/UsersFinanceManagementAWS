package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private int id;
    private String cognitoSub;
    private String email;
    private BigDecimal balance;
    private OffsetDateTime createdAt;
}