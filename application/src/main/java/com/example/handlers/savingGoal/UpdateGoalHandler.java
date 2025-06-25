package com.example.handlers.savingGoal;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.model.request.SavingGoalRequest;
import com.example.model.response.ResponseMessage;
import com.example.service.contract.SavingGoalService;

public class UpdateGoalHandler extends AbstractHandler<SavingGoalService> {

    public UpdateGoalHandler() {
        super(SavingGoalService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        int goalId = Integer.parseInt(request.getPathParameters().get("id"));
        SavingGoalRequest goalRequest = gson.fromJson(request.getBody(), SavingGoalRequest.class);

        service.updateGoal(goalRequest, userId, goalId);

        return new HandlerResponse(200, gson.toJson(new ResponseMessage("Transaction updated successfully")));
    }
}
