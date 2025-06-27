package com.example.handlers.transaction;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.model.Transaction;
import com.example.service.contract.TransactionService;

import java.util.List;
import java.util.Map;

public class GetTransactionsHandler extends AbstractHandler<TransactionService> {

    public GetTransactionsHandler() {
        super(TransactionService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request, context);

        List<Transaction> transactions;
        Map<String, String> queryParams = request.getQueryStringParameters();

        if (queryParams != null && queryParams.containsKey("categoryId")) {
            context.getLogger().log("Fetching transactions by category for user " + userId);
            int categoryId = Integer.parseInt(queryParams.get("categoryId"));
            transactions = service.getTransactionsForUserByCategory(userId, categoryId);
        } else {
            context.getLogger().log("Fetching all transactions for user " + userId);
            transactions = service.getTransactionsForUser(userId);
        }

        return new HandlerResponse(200, gson.toJson(transactions));
    }
}
