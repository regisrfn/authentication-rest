package com.rufino.server.service.impl;

import static com.rufino.server.constant.ExceptionConst.LOGIN_ATTEMPT_ERROR_MSG;
import static com.rufino.server.constant.SecurityConst.MAXIMUM_NUMBER_OF_ATTEMPTS;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.service.LoginCacheService;

import org.springframework.stereotype.Service;

@Service
public class LoginCacheServiceImpl implements LoginCacheService {

    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginCacheServiceImpl() {
        loginAttemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    @Override
    public void evictUserFromLoginCache(String username) {
        loginAttemptCache.invalidate(username);
        
    }

    @Override
    public void addUserToLoginCache(String username) {
        int attempts = 0;
        try {
            attempts = loginAttemptCache.get(username) + 1;
            loginAttemptCache.put(username, attempts);
        } catch (ExecutionException e) {
            throw new ApiRequestException(LOGIN_ATTEMPT_ERROR_MSG, INTERNAL_SERVER_ERROR);
        }
        
    }

    @Override
    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            throw new ApiRequestException(LOGIN_ATTEMPT_ERROR_MSG, INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void clearAll() {
        loginAttemptCache.invalidateAll();        
    }

   
    
}
