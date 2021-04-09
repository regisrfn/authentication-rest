package com.rufino.server.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rufino.server.domain.JwtToken;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.model.User;
import com.rufino.server.service.JwtTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
    public String getUsername(String token) {
        return jwt.getSubject(token);
    }

    @Override
    public boolean verifyToken(String token, String username) {
        try {
            return this.jwt.isTokenValid(username, token);
        } catch (JWTVerificationException e) {
            throw new ApiRequestException("Could not verify token", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public List<GrantedAuthority> getGrantedAuthorities(String token) {
        List<String> claims = jwt.getClaimsFromToken(token);
        return claims.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public Authentication getAuthentication(String username, List<GrantedAuthority> authoritiesList,
            HttpServletRequest request) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    username, null, authoritiesList);
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            return usernamePasswordAuthenticationToken;
    }

}
