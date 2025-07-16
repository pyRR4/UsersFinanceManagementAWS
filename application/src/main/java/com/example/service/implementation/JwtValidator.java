package com.example.service.implementation;

import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.JWTVerifier;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;

public class JwtValidator {

    private static final String REGION;
    private static final String USERPOOL_ID;
    private static final String ISSUER;

    private static final JwkProvider jwkProvider;

    static {
        REGION = System.getenv("COGNITO_REGION");
        USERPOOL_ID = System.getenv("COGNITO_USERPOOL_ID");

        if (REGION == null || USERPOOL_ID == null || REGION.isEmpty() || USERPOOL_ID.isEmpty()) {
            throw new IllegalStateException("Missing required environment variables: COGNITO_REGION and COGNITO_USERPOOL_ID");
        }
        ISSUER = "https://cognito-idp." + REGION + ".amazonaws.com/" + USERPOOL_ID;

        try {
            jwkProvider = new JwkProviderBuilder(new URL(ISSUER + "/.well-known/jwks.json"))
                    .cached(10, 24, TimeUnit.HOURS)
                    .build();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to build JwkProvider due to a malformed URL", e);
        }
    }

    public static DecodedJWT verifyToken(String token) throws Exception {
        DecodedJWT decodedJWT = JWT.decode(token);
        String kid = decodedJWT.getKeyId();

        RSAPublicKey publicKey = (RSAPublicKey) jwkProvider.get(kid).getPublicKey();

        Algorithm algorithm = Algorithm.RSA256(publicKey, null);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();

        return verifier.verify(token);
    }
}
