package com.rufino.server.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.rufino.server.model.User;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public interface JwtTokenService {

    public String createToken(User user);

    public boolean verifyToken(String token, String username);

    String getUsername(String token);

    List<GrantedAuthority> getGrantedAuthorities(String token);

    Authentication getAuthentication(String username, List<GrantedAuthority> authoritiesList,HttpServletRequest request);

}
