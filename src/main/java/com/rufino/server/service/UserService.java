package com.rufino.server.service;

import java.util.List;

import com.rufino.server.model.User;

import org.springframework.http.ResponseEntity;

public interface UserService {

    public User register(User user);

    public ResponseEntity<User> login(User loginUser);

    public List<User> getAllUsers();

    public User getUserById(String id);

    public boolean deleteUserById(String id);

    public User updateUser(String id, User user);

    public User getUserByEmail(String email);

    public User getUserByNickname(String username);


}
