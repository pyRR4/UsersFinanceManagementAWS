package com.example.handlers.transaction;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.model.response.ResponseMessage;
import com.example.model.request.TransactionRequest;
import com.example.service.contract.TransactionService;

public class UpdateTransactionHandler extends AbstractHandler<TransactionService> {

    public UpdateTransactionHandler() {
        super(TransactionService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request, context);
        int transactionId = Integer.parseInt(request.getPathParameters().get("id"));
        TransactionRequest transactionRequest = gson.fromJson(request.getBody(), TransactionRequest.class);

        service.updateTransaction(transactionId, userId, transactionRequest);

        return new HandlerResponse(200, gson.toJson(new ResponseMessage("Transaction updated successfully.")));
    }
}
