package com.example.handlers.transaction;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.model.response.ResponseMessage;
import com.example.model.Transaction;

import java.util.Optional;

public class GetTransactionByIdHandler extends AbstractTransactionHandler {

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        String transactionIdStr = request.getPathParameters().get("id");
        int transactionId = Integer.parseInt(transactionIdStr);

        Optional<Transaction> transactionOpt = transactionService.getTransactionById(transactionId, userId);

        return transactionOpt
                .map(transaction -> new HandlerResponse(200, gson.toJson(transaction)))
                .orElseGet(
                        () -> new HandlerResponse(
                                404,
                                gson.toJson(new ResponseMessage("Transaction not found.")
                                )
                        )
                );
    }
}
