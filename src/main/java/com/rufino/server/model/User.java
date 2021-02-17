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
import com.rufino.server.constraints.EmailConstraint;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "userEmail", name = "uk_user_email"),
        @UniqueConstraint(columnNames = "userNickname", name = "uk_user_nickname") })

@JsonInclude(Include.NON_NULL)
public class User {

    @Id
    private UUID userId;

    @EmailConstraint
    private String userEmail;

    @NotBlank(message = "Value should not be empty")
    @Column(nullable = false)
    private String userNickname, userPassword;

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
}
