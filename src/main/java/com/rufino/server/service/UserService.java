package com.rufino.server.service;

import java.util.List;

import com.rufino.server.model.User;

public interface UserService {

    public User register(User user);

    public List<User> getAllUsers();

    public User getUserById(String id);

    public boolean deleteUserById(String id);

    public User updateUser(String id, User user);

    public User getUserByEmail(String email);

    public User getUserByNickname(String username);

    public User login(String email, String password);

}
