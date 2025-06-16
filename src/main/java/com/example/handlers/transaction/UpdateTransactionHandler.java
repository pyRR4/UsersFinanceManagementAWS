package com.example.handlers.transaction;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.model.ResponseMessage;
import com.example.model.TransactionRequest;

public class UpdateTransactionHandler extends AbstractTransactionHandler {

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        int transactionId = Integer.parseInt(request.getPathParameters().get("id"));
        TransactionRequest transactionRequest = gson.fromJson(request.getBody(), TransactionRequest.class);

        transactionService.updateTransaction(transactionId, userId, transactionRequest);

        return new HandlerResponse(200, gson.toJson(new ResponseMessage("Transaction updated successfully.")));
    }
}
