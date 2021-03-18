package com.rufino.server.service;

import com.rufino.server.model.User;

public interface SecurityService {
    public String encodePassword(String password);

    public void verifyPassword(String password, String hashedPassword);

    public boolean isNotLocked(User user);

    public boolean isActive(User user);    
    
}