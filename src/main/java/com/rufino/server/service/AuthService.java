package com.rufino.server.service;

import com.rufino.server.model.User;

public interface AuthService {

    public String createToken(User user);

    public void verifyToken(String token, User user);

    public void verifyPassword(String hashedPassword, String password);

}
