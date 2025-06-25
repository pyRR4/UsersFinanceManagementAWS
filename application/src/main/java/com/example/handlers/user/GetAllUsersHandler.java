package com.example.handlers.user;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.factory.DependencyFactory;
import com.example.model.User;
import com.example.service.contract.UserService;

import java.util.List;

public class GetAllUsersHandler implements RequestHandler<Object, List<User>> {

    private final UserService userService;

    public GetAllUsersHandler() {
        this.userService = DependencyFactory.getInstance().getService(UserService.class);
    }

    @Override
    public List<User> handleRequest(Object o, Context context) {
        context.getLogger().log("Fetching all active users for forecasting.");
        List<User> users = userService.getAllUsers();
        context.getLogger().log("Found " + users.size() + " users to process.");
        return users;
    }
}
