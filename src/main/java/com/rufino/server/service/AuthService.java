package com.rufino.server.service;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.model.Token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private JWTVerifier verifier;
    private Token token;

    @Autowired
    public AuthService(JWTVerifier verifier, Token token) {
        this.verifier = verifier;
        this.token = token;
    }

    public String createToken() {
        try {
            return this.token.getToken();
        } catch (JWTCreationException exception) {
            // Invalid Signing configuration / Couldn't convert Claims.
            throw new ApiRequestException(
                    "Error on creating token. Invalid Signing configuration / Couldn't convert Claims.");
        }
    }

    public void verifyToken(String token) {
        try {
            verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new ApiRequestException("Could not verify token");
        }

    }

    public void verifyPassword(String hashedPassword, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean ok = passwordEncoder.matches(password, hashedPassword);
        if (!ok)
            throw new ApiRequestException("Could not verify token", HttpStatus.FORBIDDEN);
    }

}
