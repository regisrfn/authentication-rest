package com.rufino.server.service;

public interface SecurityService {
    public String encodePassword(String password);

    public void verifyPassword(String password, String hashedPassword);
}