package com.example.service.contract;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

public interface AuthService {
    int getUserId(APIGatewayProxyRequestEvent request, Context context);
}
