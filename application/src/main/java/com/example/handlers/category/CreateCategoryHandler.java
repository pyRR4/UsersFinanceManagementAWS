package com.example.handlers.category;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.model.request.CategoryRequest;
import com.example.model.response.ResponseMessage;
import com.example.service.contract.CategoryService;

public class CreateCategoryHandler extends AbstractHandler<CategoryService> {

    public CreateCategoryHandler() {
        super(CategoryService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request, context);
        CategoryRequest categoryRequest = gson.fromJson(request.getBody(), CategoryRequest.class);

        service.create(categoryRequest.getName(), userId);

        String responseBody = gson.toJson(new ResponseMessage("Category created successfully"));

        return new HandlerResponse(201, responseBody);
    }
}
