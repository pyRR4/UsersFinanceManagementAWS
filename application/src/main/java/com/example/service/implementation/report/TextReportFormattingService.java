package com.example.service.implementation.report;

import com.example.model.Transaction;
import com.example.service.contract.report.ReportFormattingService;

import java.util.List;

public class TextReportFormattingService implements ReportFormattingService {
    @Override
    public String format(List<Transaction> transactions, int year, int month) {
        StringBuilder reportContent = new StringBuilder("Raport Finansowy za " + year + "-" + month + "\n\n");
        reportContent.append("DATA | OPIS | KWOTA\n");
        reportContent.append("---------------------------------\n");
        for (Transaction transaction : transactions) {
            reportContent.append(String.format("%s | %s | %.2f PLN\n",
                    transaction.getDate().substring(0, 10),
                    transaction.getDescription() != null ? transaction.getDescription() : "-",
                    transaction.getAmount()));
        }

        return reportContent.toString();
    }
}
