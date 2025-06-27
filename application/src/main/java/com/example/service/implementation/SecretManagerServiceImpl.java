package com.example.service.implementation;

import com.example.service.contract.SecretManagerService;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class SecretManagerServiceImpl implements SecretManagerService {

    private final SecretsManagerClient secretsManagerClient;

    public SecretManagerServiceImpl() {
        this.secretsManagerClient = SecretsManagerClient.builder()
                .build();
    }

    @Override
    public String getSecret(String secretArn) {
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