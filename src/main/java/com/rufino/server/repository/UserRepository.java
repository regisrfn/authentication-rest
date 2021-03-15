package com.rufino.server.repository;

import java.util.List;
import java.util.UUID;

import com.rufino.server.dao.UserDao;
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
    public User insertUser(User User) {
        return jpaDataAccess.save(User);
    }

    @Override
    public boolean deleteUser(UUID id) {
        try {
            jpaDataAccess.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
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
    public User updateUser(UUID id, User User) {
        User.setUserId(id);
        return jpaDataAccess.save(User);
    }

    @Override
    public User getUserByEmail(String email) {
        return jpaDataAccess.findByEmail(email);
    }

    @Override
    public User getUserByUsername(String username) {
        return jpaDataAccess.findByUsername(username);
    }
}