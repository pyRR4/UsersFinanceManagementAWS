package com.example.service.contract;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

public interface AuthService {
    int getUserId(APIGatewayProxyRequestEvent request);
}
