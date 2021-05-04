package com.rufino.server.model;

import static com.rufino.server.constant.ProfileImageConst.DEFAULT_PROFILE_IMG;
import static com.rufino.server.enumeration.Role.ROLE_USER;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "users", uniqueConstraints = { 
        @UniqueConstraint(columnNames = "email", name = "uk_user_email"),
        @UniqueConstraint(columnNames = "username", name = "uk_user_username") 
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class User {

    @Id
    private UUID userId;

    @NotNull(message = "Value should not be empty")
    private Long userNo;

    @EmailConstraint
    @Column(nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @Column(nullable = false)
    private String profileImageUrl;

    @NotBlank(message = "Value should not be empty")
    @Column(nullable = false)
    private String username, firstName, lastName;

    @NotNull(message = "Value should not be empty")
    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime createdAt;

    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime lastLoginDate;

    @NotNull(message = "Value should not be empty")
    private boolean isActive, isLocked;

    @NotNull(message = "Invalid role value")
    private Role role;

    private UUID info;

    @ManyToMany(targetEntity = AuthorityModel.class, cascade = {CascadeType.PERSIST, 
                                                                CascadeType.DETACH,
                                                                CascadeType.MERGE,
                                                                CascadeType.REFRESH} )
    private List<AuthorityModel> authorities;

    public User() {
        setUserId(UUID.randomUUID());
        setCreatedAt(ZonedDateTime.now());
        setActive(true);
        setLocked(false);
        setRole(ROLE_USER);
        this.userNo = (long) Math.floor(100E3 + Math.random() * 899999);
        setProfileImageUrl(DEFAULT_PROFILE_IMG + this.userNo);
    }

    public void setAuthorities(Authority... authorities){}

    public List<Authority> getAuthorities() {
        return this.authorities.stream().map(auth -> {
            return auth.getAuthority();
        }).collect(Collectors.toList());
    }

    public void setAuthoritiesList(Authority... authorities) {
        List<Authority> aList = Arrays.asList(authorities);
        this.authorities = aList.stream().map(auth -> {
            return new AuthorityModel(auth);
        }).collect(Collectors.toList());
    }

    public void setRole(String role) {
        try {
            this.role = Role.valueOf(role.toUpperCase());
            setAuthoritiesList(this.role.getAuthorities());
        } catch (Exception e) {
            e.printStackTrace();
            this.role = null;
        }
    }

    public void setRole(Role role) {
        this.role = role;
        setAuthoritiesList(this.role.getAuthorities());
    }
}
