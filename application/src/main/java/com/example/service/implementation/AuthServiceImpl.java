package com.example.service.implementation;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.repository.contract.UserRepository;
import com.example.service.contract.AuthService;
import com.example.model.User;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final Gson gson;

    @Override
    public int getUserId(APIGatewayProxyRequestEvent request, Context context) {String authHeader = request.getHeaders().get("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring("Bearer ".length());

        try {
            DecodedJWT jwt = JwtValidator.verifyToken(token);

            String sub = jwt.getClaim("sub").asString();
            String email = jwt.getClaim("email").asString();

            context.getLogger().log("User sub: " + sub + ", email: " + email);

            return userRepository.findByCognitoSub(sub)
                    .map(User::getId)
                    .orElseGet(() -> userRepository.create(sub, email).getId());

        } catch (JWTVerificationException ex) {
            context.getLogger().log("Invalid token: " + ex.getMessage());
            throw new RuntimeException("Unauthorized");
        } catch (Exception ex) {
            context.getLogger().log("Token verification error: " + ex.getMessage());
            throw new RuntimeException("Unauthorized");
        }
    }

}
