package com.rufino.server.service.impl;

import static com.rufino.server.constant.SecurityConst.JWT_TOKEN_HEADER;

import java.util.List;
import java.util.UUID;

import com.rufino.server.dao.UserDao;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.exception.domain.InvalidCredentialsException;
import com.rufino.server.model.User;
import com.rufino.server.service.JwtTokenService;
import com.rufino.server.service.SecurityService;
import com.rufino.server.service.UserService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private SecurityService securityService;
    private JwtTokenService jwtTokenService;

    @Autowired
    public UserServiceImpl(UserDao userDao, SecurityService securityService, JwtTokenService jwtTokenService) {
        this.userDao = userDao;
        this.securityService = securityService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public User register(User user) {
        encodePassword(user);
        User savedUser = userDao.insertUser(user);
        savedUser.setPassword(null);
        return savedUser;
    }

    @Override
    public ResponseEntity<User> login(User loginUser) {
        User user = null;

        if (StringUtils.isNotBlank(loginUser.getUsername())) {
            user = userDao.getUserByUsername(loginUser.getUsername());
        } else if (StringUtils.isBlank(loginUser.getEmail()))
            throw new InvalidCredentialsException();
        else {
            user = userDao.getUserByEmail(loginUser.getEmail());
        }

        if (user == null)
            throw new InvalidCredentialsException();

        authenticate(user,loginUser.getPassword());
        HttpHeaders jwtHeaders = getJwtHeader(user);
        return new ResponseEntity<>(user, jwtHeaders, HttpStatus.OK);
    }

    private void authenticate(User user, String rawPassword) {
        securityService.verifyPassword(rawPassword, user.getPassword());
        securityService.isActive(user);
        securityService.isNotLocked(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAll();
    }

    @Override
    public User getUserById(String id) {
        try {
            UUID userId = UUID.fromString(id);
            User user = userDao.getUser(userId);
            if (user == null)
                throw new ApiRequestException("User not found", HttpStatus.NOT_FOUND);
            return user;
        } catch (IllegalArgumentException e) {
            throw new ApiRequestException("Invalid User UUID format", HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public boolean deleteUserById(String id) {
        try {
            UUID userId = UUID.fromString(id);
            boolean ok = userDao.deleteUser(userId);
            if (!ok)
                throw new ApiRequestException("User not found", HttpStatus.NOT_FOUND);
            return ok;
        } catch (IllegalArgumentException e) {
            throw new ApiRequestException("Invalid User UUID format", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public User updateUser(String id, User user) {
        try {
            UUID userId = UUID.fromString(id);
            return userDao.updateUser(userId, user);
        } catch (IllegalArgumentException e) {
            throw new ApiRequestException("Invalid User UUID format", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userDao.getUserByEmail(email);
        if (user == null)
            throw new ApiRequestException("User not found", HttpStatus.NOT_FOUND);
        return user;
    }

    @Override
    public User getUserByNickname(String username) {
        User user = userDao.getUserByUsername(username);
        if (user == null)
            throw new ApiRequestException("User not found", HttpStatus.NOT_FOUND);
        return user;
    }

    private void encodePassword(User user) {
        String hashedPassword = securityService.encodePassword(user.getPassword());
        user.setPassword(hashedPassword);
    }

    private HttpHeaders getJwtHeader(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenService.createToken(user));
        return headers;
    }
}