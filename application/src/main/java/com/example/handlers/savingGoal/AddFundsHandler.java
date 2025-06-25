package com.example.handlers.savingGoal;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.model.request.AddFundsRequest;
import com.example.model.response.ResponseMessage;
import com.example.service.contract.SavingGoalService;

public class AddFundsHandler extends AbstractHandler<SavingGoalService> {

    public AddFundsHandler() {
        super(SavingGoalService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        int goalId = Integer.parseInt(request.getPathParameters().get("id"));
        AddFundsRequest addFundsRequest = gson.fromJson(request.getBody(), AddFundsRequest.class);

        service.addFundsToGoal(goalId, userId, addFundsRequest.getAmountToAdd());

        return new HandlerResponse(200, gson.toJson(
                new ResponseMessage("Funds added successfully to goal " + goalId))
        );
    }
}
