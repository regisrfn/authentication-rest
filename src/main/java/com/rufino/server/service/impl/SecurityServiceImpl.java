package com.rufino.server.service.impl;

import com.rufino.server.exception.domain.AccountDisabledException;
import com.rufino.server.exception.domain.AccountLockedException;
import com.rufino.server.exception.domain.InvalidCredentialsException;
import com.rufino.server.model.User;
import com.rufino.server.service.LoginCacheService;
import com.rufino.server.service.SecurityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    private LoginCacheService loginCacheService;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public SecurityServiceImpl(LoginCacheService loginCacheService) {
        this.loginCacheService = loginCacheService;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);

    }

    @Override
    public void verifyPassword(User user, String notEncodedPassword) {
        boolean ok = passwordEncoder.matches(notEncodedPassword, user.getPassword());
        if (!ok) {
            this.loginCacheService.addUserToLoginCache(user.getUsername());
            throw new InvalidCredentialsException();
        }
        this.loginCacheService.evictUserFromLoginCache(user.getUsername());
    }

    @Override
    public boolean isNotLocked(User user) {
        if (user.isLocked())
            throw new AccountLockedException();
        return true;
    }

    @Override
    public boolean isActive(User user) {
        if (!user.isActive())
            throw new AccountDisabledException();
        return true;
    }

}
