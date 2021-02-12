package com.rufino.server.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "userEmail", name = "uk_user_email"),
        @UniqueConstraint(columnNames = "userNickname", name = "uk_user_nickname") })

@JsonInclude(Include.NON_NULL)
public class User {

    @Id
    private UUID userId;

    @NotBlank(message = "Value should not be empty")
    @Column(nullable = false)
    private String userEmail;

    @NotBlank(message = "Value should not be empty")
    @Column(nullable = false)
    private String userNickname;

    @NotBlank(message = "Value should not be empty")
    @Column(nullable = false)
    private String userPassword;

    @NotNull(message = "Value should not be empty")
    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime createdAt;

    private UUID userInfo;

    @Transient
    private String token;

    public User() {
        setUserId(UUID.randomUUID());
        setCreatedAt(ZonedDateTime.now(ZoneId.of("Z")));
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UUID userInfo) {
        this.userInfo = userInfo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
