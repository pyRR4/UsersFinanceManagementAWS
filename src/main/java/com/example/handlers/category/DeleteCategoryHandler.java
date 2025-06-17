package com.example.handlers.category;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.service.contract.CategoryService;

public class DeleteCategoryHandler extends AbstractHandler<CategoryService> {

    public DeleteCategoryHandler() {
        super(CategoryService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        int categoryId = Integer.parseInt(request.getPathParameters().get("id"));

        service.deleteByIdAndUserId(userId, categoryId);

        return new HandlerResponse(204, "");
    }
}
