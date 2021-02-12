package com.rufino.server.service;

import java.util.List;
import java.util.UUID;

import com.rufino.server.dao.UserDao;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.model.User;
import com.rufino.server.validation.ValidateEmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserDao userDao;
    private ValidateEmail validateEmail;
    private AuthService authService;

    @Autowired
    public UserService(UserDao userDao, ValidateEmail validateEmail, AuthService authService) {
        this.validateEmail = validateEmail;
        this.userDao = userDao;
        this.authService = authService;
    }

    public User saveUser(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(hashedPassword);
        if (!validateEmail.test(user.getUserEmail()))
            throw new ApiRequestException("Invalid email format", "userEmail", HttpStatus.BAD_REQUEST);

        User savedUser = userDao.insertUser(user);
        savedUser.setUserPassword(null);
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public User getUserByEmail(String email) {
        User user = userDao.getUserByEmail(email);
        if (user == null)
            throw new ApiRequestException("User not found", HttpStatus.NOT_FOUND);
        return user;
    }

    public User getUserByNickname(String nickname) {
        User user = userDao.getUserByNickname(nickname);
        if (user == null)
            throw new ApiRequestException("User not found", HttpStatus.NOT_FOUND);
        return user;
    }

    public User login(String email, String password) {
        User user = userDao.getUserByEmail(email);
        if (user == null)
            throw new ApiRequestException("Authentication failed", HttpStatus.FORBIDDEN);
        authService.verifyPassword(user.getUserPassword(), password);
        user.setToken(authService.createToken());
        return user;
    }
}