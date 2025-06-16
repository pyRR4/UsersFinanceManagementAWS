package com.example.handlers.transaction;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.model.Transaction;

import java.util.List;
import java.util.Map;

public class GetTransactionsHandler extends AbstractTransactionHandler{

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);

        List<Transaction> transactions;
        Map<String, String> queryParams = request.getQueryStringParameters();

        if (queryParams != null && queryParams.containsKey("categoryId")) {
            context.getLogger().log("Fetching transactions by category for user " + userId);
            int categoryId = Integer.parseInt(queryParams.get("categoryId"));
            transactions = transactionService.getTransactionsForUserByCategory(userId, categoryId);
        } else {
            context.getLogger().log("Fetching all transactions for user " + userId);
            transactions = transactionService.getTransactionsForUser(userId);
        }

        return new HandlerResponse(200, gson.toJson(transactions));
    }
}
