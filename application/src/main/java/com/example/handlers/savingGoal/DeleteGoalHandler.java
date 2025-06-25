package com.example.handlers.savingGoal;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.service.contract.SavingGoalService;

public class DeleteGoalHandler extends AbstractHandler<SavingGoalService> {

    public DeleteGoalHandler() {
        super(SavingGoalService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        int goalId = Integer.parseInt(request.getPathParameters().get("id"));

        service.deleteGoal(userId, goalId);

        return new HandlerResponse(204, "");
    }
}
