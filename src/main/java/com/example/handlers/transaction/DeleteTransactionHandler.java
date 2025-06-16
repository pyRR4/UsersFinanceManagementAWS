package com.example.handlers.transaction;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

public class DeleteTransactionHandler extends AbstractTransactionHandler {

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        int transactionId = Integer.parseInt(request.getPathParameters().get("id"));

        transactionService.deleteByIdAndUserId(transactionId, userId);

        return new HandlerResponse(204, "");
    }
}
