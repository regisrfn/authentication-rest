package com.rufino.server.service.impl;

import com.rufino.server.exception.domain.InvalidCredentialsException;
import com.rufino.server.service.SecurityService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Override
    public String encodePassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);

    }

    @Override
    public void verifyPassword(String password, String hashedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean ok = passwordEncoder.matches(password, hashedPassword);
        if (!ok)
            throw new InvalidCredentialsException();
    }

}
