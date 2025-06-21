package com.example.service.implementation.report;

import com.example.model.User;
import com.example.service.contract.UserService;
import com.example.service.contract.report.ReportingJobService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ReportingJobServiceImpl implements ReportingJobService {

    private final UserService userService;
    private final SqsClient sqsClient;
    private final Gson gson;
    private final String queueUrl;

    @Override
    public int dispatchMonthlyJobs() {
        List<User> users = userService.getAllUsers();
        YearMonth reportPeriod = YearMonth.now().minusMonths(1);

        for (User user : users) {
            Map<String, Object> messageBody = Map.of(
                    "userId", user.getId(),
                    "userEmail", user.getEmail(),
                    "year", reportPeriod.getYear(),
                    "month", reportPeriod.getMonthValue()
            );

            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(gson.toJson(messageBody))
                    .build());
        }
        return users.size();
    }
}
