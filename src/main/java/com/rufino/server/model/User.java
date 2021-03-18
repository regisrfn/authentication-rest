package com.rufino.server.model;

import static com.rufino.server.enumeration.Role.ROLE_USER;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.rufino.server.constraints.EmailConstraint;
import com.rufino.server.enumeration.Authority;
import com.rufino.server.enumeration.Role;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "email", name = "uk_user_email"),
        @UniqueConstraint(columnNames = "username", name = "uk_user_username") })

@JsonInclude(Include.NON_NULL)
public class User {

    @Id
    private UUID userId;

    @NotNull(message = "Value should not be empty")
    private Long userNo;

    @EmailConstraint
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Value should not be empty")
    @Column(nullable = false)
    private String username, password, firstName, lastName;

    @NotNull(message = "Value should not be empty")
    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime createdAt;

    @NotNull(message = "Value should not be empty")
    private boolean isActive, isLocked;

    @NotNull(message = "Value should not be empty")
    private Role role;

    private UUID info;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<AuthorityModel> authorityList;

    public User() {
        setUserId(UUID.randomUUID());
        setCreatedAt(ZonedDateTime.now(ZoneId.of("Z")));
        setActive(true);
        setLocked(false);
        setRole(ROLE_USER);
        setAuthorityList(ROLE_USER.getAuthorities());
        this.userNo = (long) Math.floor(100E3 + Math.random() * 899999);
    }

    public List<Authority> getAuthorityList() {
        return this.authorityList.stream().map(auth -> {
            return auth.getAuthority();
        }).collect(Collectors.toList());
    }

    public void setAuthorityList(Authority... authorities) {
        List<Authority> aList = Arrays.asList(authorities);
        this.authorityList = aList.stream().map(auth -> {
            return new AuthorityModel(auth);
        }).collect(Collectors.toList());
    }
}
