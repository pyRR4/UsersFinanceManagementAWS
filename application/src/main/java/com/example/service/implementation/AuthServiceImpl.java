package com.example.service.implementation;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.repository.contract.UserRepository;
import com.example.service.contract.AuthService;
import com.example.model.User;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public int getUserId(APIGatewayProxyRequestEvent request) {
        Map<String, Object> authorizer = request.getRequestContext().getAuthorizer();
        if (authorizer == null || !authorizer.containsKey("claims")) {
            throw new RuntimeException("Authorization context not found.");
        }

        Map<String, String> claims = (Map<String, String>) authorizer.get("claims");
        String cognitoSub = claims.get("sub");
        String email = claims.get("email");

        if (cognitoSub == null || email == null) {
            throw new RuntimeException("Cognito 'sub' or 'email' claim not found in token.");
        }

        return userRepository.findByCognitoSub(cognitoSub)
                .map(User::getId)
                .orElseGet(() -> userRepository.create(cognitoSub, email).getId());
    }
}
