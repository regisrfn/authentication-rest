package com.rufino.server.service;

import com.rufino.server.model.User;

public interface JwtTokenService {

    public String createToken(User user);

    public boolean verifyToken(String token, String username);

    String getUsername(String token);

}
