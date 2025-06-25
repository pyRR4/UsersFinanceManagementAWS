package com.example.handlers.raport;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.factory.DependencyFactory;
import com.example.service.contract.report.ReportingJobService;

import java.util.Map;

/**
 * Handler uruchamiany przez EventBridge Scheduler.
 * Jego jedynym zadaniem jest delegowanie pracy do ReportingJobService.
 */
public class StartReportGenerationHandler implements RequestHandler<Map<String, Object>, Void> {

    private final ReportingJobService reportingJobService;

    public StartReportGenerationHandler() {
        // Pobieramy jedną, konkretną zależność z naszej fabryki
        this.reportingJobService = DependencyFactory.getInstance().getService(ReportingJobService.class);
    }

    @Override
    public Void handleRequest(Map<String, Object> event, Context context) {
        context.getLogger().log("Scheduler triggered. Starting to dispatch reporting jobs...");

        int jobsDispatched = reportingJobService.dispatchMonthlyJobs();

        context.getLogger().log("Process finished. Dispatched " + jobsDispatched + " jobs.");
        return null;
    }
}