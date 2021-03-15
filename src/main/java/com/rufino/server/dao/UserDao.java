package com.rufino.server.dao;

import java.util.List;
import java.util.UUID;

import com.rufino.server.model.User;

public interface UserDao {
    User insertUser(User User);

    boolean deleteUser(UUID id);

    List<User> getAll();

    User getUser(UUID id);

    User getUserByEmail(String email);

    User getUserByUsername(String username);

    User updateUser(UUID id, User User);
}