package com.example.handlers.category;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.model.response.ResponseMessage;
import com.example.service.contract.CategoryService;

public class UpdateCategoryHandler extends AbstractHandler<CategoryService> {

    public UpdateCategoryHandler() {
        super(CategoryService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        int categoryId = Integer.parseInt(request.getPathParameters().get("id"));
        String name = request.getPathParameters().get("name");

        service.update(categoryId, name, userId);

        return new HandlerResponse(200, gson.toJson(new ResponseMessage("Transaction updated successfully.")));
    }
}
