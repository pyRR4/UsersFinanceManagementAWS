package com.example.handlers.raport;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.example.factory.DependencyFactory;
import com.example.model.Transaction;
import com.example.service.contract.TransactionService;
import com.example.service.contract.report.ReportDistributionService;
import com.example.service.contract.report.ReportFormattingService;
import com.google.gson.Gson;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.List;
import java.util.Map;

public class ReportGenerationWorkerHandler implements RequestHandler<SQSEvent, Void> {

    private final TransactionService transactionService;
    private final ReportDistributionService reportDistributionService;
    private final ReportFormattingService reportFormattingService;
    private final Gson gson;


    public ReportGenerationWorkerHandler() {
        DependencyFactory dependencyFactory = DependencyFactory.getInstance();
        this.transactionService = dependencyFactory.getService(TransactionService.class);
        this.reportDistributionService = dependencyFactory.getService(ReportDistributionService.class);
        this.reportFormattingService = dependencyFactory.getService(ReportFormattingService.class);
        this.gson = dependencyFactory.getService(Gson.class);
    }


    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
        for (SQSEvent.SQSMessage msg : sqsEvent.getRecords()) {
            try {
                Map<String, Object> body = gson.fromJson(msg.getBody(), Map.class);
                int userId = ((Double) body.get("userId")).intValue();
                int year = ((Double) body.get("year")).intValue();
                int month = ((Double) body.get("month")).intValue();
                String userEmail = (String) body.get("userEmail");

                context.getLogger().log("Generating report for user " + userId + " for period " + year + "-" + month);

                List<Transaction> transactions = transactionService.getTransactionsForUser(userId);

                String reportContent = reportFormattingService.format(transactions, year, month);

                String reportKey = String.format("%d/raport-%d-%d.txt", userId, year, month);
                reportDistributionService.distribute(reportContent, reportKey, userEmail);
                context.getLogger().log("Successfully generated report for user " + userId);
            } catch (Exception e) {
                context.getLogger().log("ERROR processing SQS message: " + e);
            }
        }
        return null;
    }
}
