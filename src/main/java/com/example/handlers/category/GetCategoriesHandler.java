package com.example.handlers.category;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.model.Category;
import com.example.service.contract.CategoryService;

import java.util.List;

public class GetCategoriesHandler extends AbstractHandler<CategoryService> {

    public GetCategoriesHandler() {
        super(CategoryService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);

        List<Category> categories = service.findAllByUserId(userId);

        return new HandlerResponse(200, gson.toJson(categories));
    }
}
