package com.example.handlers.transaction;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.service.contract.TransactionService;

public class DeleteTransactionHandler extends AbstractHandler<TransactionService> {

    public DeleteTransactionHandler() {
        super(TransactionService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        int transactionId = Integer.parseInt(request.getPathParameters().get("id"));

        service.deleteByIdAndUserId(transactionId, userId);

        return new HandlerResponse(204, "");
    }
}
