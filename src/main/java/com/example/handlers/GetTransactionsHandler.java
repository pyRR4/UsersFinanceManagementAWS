package com.example.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.factory.DependencyFactory;
import com.example.model.ResponseMessage;
import com.example.model.Transaction;
import com.example.service.contract.AuthService;
import com.example.service.contract.TransactionService;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class GetTransactionsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AuthService authService;
    private final TransactionService transactionService;
    private final Gson gson;

    public GetTransactionsHandler() {
        DependencyFactory factory = DependencyFactory.getInstance();
        this.authService = factory.getAuthService();
        this.transactionService = factory.getTransactionService();
        this.gson = factory.getGson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setHeaders(Map.of("Content-Type", "application/json", "Access-Control-Allow-Origin", "*"));

        try {
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

            response.setStatusCode(200);
            response.setBody(gson.toJson(transactions));

        } catch (NumberFormatException e) {
            context.getLogger().log("ERROR: Invalid categoryId format - " + e.getMessage());
            response.setStatusCode(400);
            response.setBody(gson.toJson(new ResponseMessage("Invalid categoryId format. It must be a number.")));
        } catch (Exception e) {
            context.getLogger().log("ERROR: " + e.toString());
            response.setStatusCode(500);
            response.setBody(gson.toJson(new ResponseMessage("Internal Server Error: " + e.getMessage())));
        }

        return response;
    }
}
