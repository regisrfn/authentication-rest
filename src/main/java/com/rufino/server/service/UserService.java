package com.rufino.server.service;

import java.util.List;
import java.util.UUID;

import com.rufino.server.dao.UserDao;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserDao userDao;
    private AuthService authService;

    @Autowired
    public UserService(UserDao userDao, AuthService authService) {
        this.userDao = userDao;
        this.authService = authService;
    }

    public User saveUser(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(hashedPassword);
        User savedUser = userDao.insertUser(user);

        savedUser.setPassword(null);
        savedUser.setCreatedAt(null);
        return savedUser;
    }

    public List<User> getAllUsers() {
        return userDao.getAll();
    }

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

    public User updateUser(String id, User user) {
        try {
            UUID userId = UUID.fromString(id);
            return userDao.updateUser(userId, user);
        } catch (IllegalArgumentException e) {
            throw new ApiRequestException("Invalid User UUID format", HttpStatus.BAD_REQUEST);
        }
    }

    public User getUserByEmail(String email) {
        User user = userDao.getUserByEmail(email);
        if (user == null)
            throw new ApiRequestException("User not found", HttpStatus.NOT_FOUND);
        return user;
    }

    public User getUserByNickname(String username) {
        User user = userDao.getUserByUsername(username);
        if (user == null)
            throw new ApiRequestException("User not found", HttpStatus.NOT_FOUND);
        return user;
    }

    public User login(String email, String password) {
        User user = userDao.getUserByEmail(email);
        if (user == null)
            throw new ApiRequestException("Authentication failed", HttpStatus.FORBIDDEN);
        authService.verifyPassword(user.getPassword(), password);
        user.setToken(authService.createToken(user));
        return user;
    }
}