package com.api.auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String identifier, String subject) throws IllegalArgumentException, JWTCreationException {
        return JWT.create()
                .withSubject(subject)
                .withClaim("identifier", identifier)
                .withIssuedAt(new Date())
                .withIssuer("Cash Manager")
                .sign(Algorithm.HMAC256(secret));
    }

    public String getSubjectFromToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);
        return jwt.getSubject();
    }

    public String validateTokenAndRetrieveSubject(String token, String subject) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject(subject)
                .withIssuer("Cash Manager")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("identifier").asString();
    }

}