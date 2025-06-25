package com.example.service.implementation;

import com.example.service.contract.NotificationService;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@RequiredArgsConstructor
public class SnsNotificationService implements NotificationService {

    private final SnsClient snsClient;
    private final String topicArn;

    @Override
    public void send(String subject, String message) {
        snsClient.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .subject(subject)
                .message(message)
                .build());
    }
}
