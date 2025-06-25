package com.example.service.implementation.report;

import com.example.service.contract.NotificationService;
import com.example.service.contract.StorageService;
import com.example.service.contract.report.ReportDistributionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReportDistributionServiceImpl implements ReportDistributionService {

    private final StorageService storageService;
    private final NotificationService notificationService;

    @Override
    public void distribute(String reportContent, String reportKey, String userEmail) {
        storageService.upload(reportKey, reportContent);

        String subject = "Twój miesięczny raport finansowy";
        String message = "Twój raport finansowy jest gotowy! Możesz go znaleźć w panelu aplikacji.";
        notificationService.send(subject, message);
    }
}
