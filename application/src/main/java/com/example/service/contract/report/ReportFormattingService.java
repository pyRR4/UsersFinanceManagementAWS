package com.example.service.contract.report;

import com.example.model.Transaction;

import java.util.List;

public interface ReportFormattingService {
    String format(List<Transaction> transactions, int year, int month);
}
