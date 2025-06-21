package com.example.service.contract.report;

public interface ReportDistributionService {
    void distribute(String reportContent, String reportKey, String userEmail);
}
