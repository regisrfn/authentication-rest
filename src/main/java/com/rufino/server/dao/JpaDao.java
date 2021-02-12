package com.rufino.server.dao;

import java.util.UUID;

import com.rufino.server.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDao extends JpaRepository<User, UUID> {
    User findByUserEmail(String email);
    User findByUserNickname(String nickname);
}