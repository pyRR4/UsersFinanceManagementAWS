package com.example.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.exception.ResourceNotFoundException;
import com.example.factory.DependencyFactory;
import com.example.model.response.ResponseMessage;
import com.example.service.contract.AuthService;
import com.google.gson.Gson;

import java.util.Map;

public abstract class AbstractHandler<T> implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    protected final AuthService authService;
    protected final Gson gson;
    protected final T service;

    public record HandlerResponse(int statusCode, String body) {}

    public AbstractHandler(Class<T> serviceClass) {
        DependencyFactory factory = DependencyFactory.getInstance();
        this.service = factory.getService(serviceClass);
        this.authService = factory.getService(AuthService.class);
        this.gson = factory.getService(Gson.class);
    }

    @Override
    public final APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            HandlerResponse response = handleRequestLogic(request, context);
            return createSuccessResponse(response.statusCode(), response.body());
        } catch (ResourceNotFoundException e) {
            context.getLogger().log("NOT FOUND: " + e.getMessage());
            return createErrorResponse(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            context.getLogger().log("BAD REQUEST: " + e.getMessage());
            return createErrorResponse(400, e.getMessage());
        } catch (Exception e) {
            context.getLogger().log("INTERNAL SERVER ERROR: " + e.toString());
            return createErrorResponse(500, "Internal Server Error");
        }
    }

    protected abstract HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context);

    private APIGatewayProxyResponseEvent createSuccessResponse(int statusCode, String body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setHeaders(Map.of("Content-Type", "application/json", "Access-Control-Allow-Origin", "*"));
        response.setStatusCode(statusCode);
        response.setBody(body);
        return response;
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setHeaders(Map.of("Content-Type", "application/json", "Access-Control-Allow-Origin", "*"));
        response.setStatusCode(statusCode);
        response.setBody(gson.toJson(new ResponseMessage(message)));
        return response;
    }
}
