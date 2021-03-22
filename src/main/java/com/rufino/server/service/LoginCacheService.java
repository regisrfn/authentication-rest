package com.rufino.server.service;

public interface LoginCacheService {
    public void evictUserFromLoginCache(String username);

    public void addUserToLoginCache(String username);

    public boolean hasExceededMaxAttempts(String username);

    public void clearAll();
}
