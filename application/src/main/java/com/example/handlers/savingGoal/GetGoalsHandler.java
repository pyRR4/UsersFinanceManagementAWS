package com.example.handlers.savingGoal;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.model.SavingGoal;
import com.example.service.contract.SavingGoalService;

import java.util.List;

public class GetGoalsHandler extends AbstractHandler<SavingGoalService> {

    public GetGoalsHandler() {
        super(SavingGoalService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        List<SavingGoal> goals = service.getGoalsForUser(userId);

        return new HandlerResponse(200, gson.toJson(goals));
    }
}
