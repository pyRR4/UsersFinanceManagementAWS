package com.example.handlers.transaction;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.model.response.ResponseMessage;
import com.example.model.request.TransactionRequest;
import com.example.service.contract.TransactionService;

public class CreateTransactionHandler extends AbstractHandler<TransactionService> {

    public CreateTransactionHandler() {
        super(TransactionService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        TransactionRequest transactionRequest = gson.fromJson(request.getBody(), TransactionRequest.class);
        service.createTransaction(transactionRequest, userId);

        String responseBody = gson.toJson(new ResponseMessage("Transaction created successfully"));

        return new HandlerResponse(201, responseBody);
    }
}
