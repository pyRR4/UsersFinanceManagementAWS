package com.example.handlers.transaction;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.model.response.ResponseMessage;
import com.example.model.Transaction;
import com.example.service.contract.TransactionService;

import java.util.Optional;

public class GetTransactionByIdHandler extends AbstractHandler<TransactionService> {

    public GetTransactionByIdHandler() {
        super(TransactionService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request, context);
        String transactionIdStr = request.getPathParameters().get("id");
        int transactionId = Integer.parseInt(transactionIdStr);

        Optional<Transaction> transactionOpt = service.getTransactionById(transactionId, userId);

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
