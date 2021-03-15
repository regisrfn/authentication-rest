package com.rufino.server.service.impl;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.domain.JwtToken;
import com.rufino.server.model.User;
import com.rufino.server.service.JwtTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private JwtToken jwt;

    @Autowired
    public JwtTokenServiceImpl(JwtToken jwt) {
        this.jwt = jwt;
    }

    @Override
    public String createToken(User user) {
        try {
            return this.jwt.generateToken(user);
        } catch (JWTCreationException exception) {
            // Invalid Signing configuration / Couldn't convert Claims.
            throw new ApiRequestException(
                    "Error on creating token. Invalid Signing configuration / Couldn't convert Claims.");
        }
    }

    @Override
    public void verifyPassword(String hashedPassword, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean ok = passwordEncoder.matches(password, hashedPassword);
        if (!ok)
            throw new ApiRequestException("Username / password incorrect. Please try again", HttpStatus.FORBIDDEN);
    }

    @Override
    public String getUsername(String token) {
        return jwt.getSubject(token);
    }

    @Override
    public void verifyToken(String token, String username) {
        try {
            this.jwt.isTokenValid(username, token);
        } catch (JWTVerificationException e) {
            throw new ApiRequestException("Could not verify token", HttpStatus.FORBIDDEN);
        }
    }

}
