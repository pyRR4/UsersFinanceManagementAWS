package com.example.service.implementation;

import com.example.service.contract.SecretsManagerService;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class SecretsManagerServiceImpl implements SecretsManagerService {

    private final SecretsManagerClient secretsManagerClient;

    public SecretsManagerServiceImpl() {
        this.secretsManagerClient = SecretsManagerClient.builder().build();
    }

    @Override
    public String getParameter(String secretArn) {
        if (secretArn == null || secretArn.isBlank()) {
            throw new IllegalArgumentException("Secret ARN cannot be null or empty.");
        }
        try {
            GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                    .secretId(secretArn)
                    .build();
            GetSecretValueResponse valueResponse = secretsManagerClient.getSecretValue(valueRequest);
            return valueResponse.secretString();

        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve secret from AWS Secrets Manager", e);
        }
    }
}