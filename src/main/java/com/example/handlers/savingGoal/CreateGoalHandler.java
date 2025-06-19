package com.example.handlers.savingGoal;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.handlers.AbstractHandler;
import com.example.model.request.SavingGoalRequest;
import com.example.model.response.ResponseMessage;
import com.example.service.contract.SavingGoalService;

public class CreateGoalHandler extends AbstractHandler<SavingGoalService> {

    public CreateGoalHandler() {
        super(SavingGoalService.class);
    }

    @Override
    protected HandlerResponse handleRequestLogic(APIGatewayProxyRequestEvent request, Context context) {
        int userId = authService.getUserId(request);
        SavingGoalRequest savingGoalRequest = gson.fromJson(request.getBody(), SavingGoalRequest.class);

        service.createGoal(savingGoalRequest, userId);

        String responseBody = gson.toJson(new ResponseMessage("Saving goal created successfully"));

        return new HandlerResponse(201, responseBody);
    }
}
