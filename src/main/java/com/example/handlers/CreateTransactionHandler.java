package com.example.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.config.DatabaseConfig;
import com.example.model.ResponseMessage;
import com.example.model.TransactionRequest;
import com.example.repository.implementation.TransactionRepositoryImpl;
import com.example.repository.implementation.UserRepositoryImpl;
import com.example.repository.contract.TransactionRepository;
import com.example.repository.contract.UserRepository;
import com.example.service.contract.AuthService;
import com.example.service.contract.TransactionService;
import com.example.service.implementation.AuthServiceImpl;
import com.example.service.implementation.TransactionServiceImpl;
import com.google.gson.Gson;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;

import java.util.Map;

public class CreateTransactionHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson gson = new Gson();
    private static final AuthService authService;
    private static final TransactionService transactionService;

    static {
        DatabaseConfig dbConfig = new DatabaseConfig();
        RdsDataClient rdsDataClient = RdsDataClient.builder().build();

        UserRepository userRepository = new UserRepositoryImpl(rdsDataClient, dbConfig);
        TransactionRepository transactionRepository = new TransactionRepositoryImpl(rdsDataClient, dbConfig);

        authService = new AuthServiceImpl(userRepository);
        transactionService = new TransactionServiceImpl(transactionRepository);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request,
            Context context
    ) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setHeaders(Map.of("Content-Type", "application/json", "Access-Control-Allow-Origin", "*"));

        try {
            int userId = authService.getUserId(request);

            TransactionRequest transactionRequest = gson.fromJson(request.getBody(), TransactionRequest.class);
            transactionService.createTransaction(transactionRequest, userId);

            response.setStatusCode(201);
            response.setBody(gson.toJson(new ResponseMessage("Transaction created successfully")));
        } catch (Exception e) {
            context.getLogger().log("ERROR: " + e.toString());
            response.setStatusCode(500);
            response.setBody(gson.toJson(new ResponseMessage("Internal Server Error: " + e.getMessage())));
        }

        return response;
    }
}
