package com.example.handlers.transaction;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.model.ResponseMessage;
import com.example.model.TransactionRequest;

public class CreateTransactionHandler extends AbstractTransactionHandler {

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        TransactionRequest transactionRequest = gson.fromJson(request.getBody(), TransactionRequest.class);
        transactionService.createTransaction(transactionRequest, userId);

        String responseBody = gson.toJson(new ResponseMessage("Transaction created successfully"));

        return new HandlerResponse(201, responseBody);
    }
}
