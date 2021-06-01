package com.rufino.server.repository;

import java.util.List;
import java.util.UUID;

import com.rufino.server.dao.UserDao;
import com.rufino.server.exception.domain.UserNotFoundException;
import com.rufino.server.dao.JpaDao;
import com.rufino.server.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements UserDao {

    private JpaDao jpaDataAccess;

    @Autowired
    public UserRepository(JpaDao jpaDataAccess, JdbcTemplate jdbcTemplate) {
        this.jpaDataAccess = jpaDataAccess;
    }

    @Override
    public User insertUser(User user) {
        return jpaDataAccess.save(user);
    }

    @Override
    public boolean deleteUser(UUID id) {
        try {
            jpaDataAccess.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public List<User> getAll() {
        return jpaDataAccess.findAll();
    }

    @Override
    public User getUser(UUID id) {
        return jpaDataAccess.findById(id).orElse(null);
    }

    @Override
    public User updateUser(UUID id, User user) {
        user.setUserId(id);
        return jpaDataAccess.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return jpaDataAccess.findByEmail(email);
    }

    @Override
    public User getUserByUsername(String username) {
        return jpaDataAccess.findByUsername(username);
    }

    @Override
    public User updateUser(User user) throws UserNotFoundException {
        try {
            User oldUser = jpaDataAccess.findById(user.getUserId()).orElseThrow();
            user.setPassword(oldUser.getPassword());
            user.setProfileImageUrl(oldUser.getProfileImageUrl());
        } catch (Exception e) {
            throw new UserNotFoundException();
        }
        return jpaDataAccess.save(user);
    }
}