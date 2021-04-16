package com.rufino.server.service.impl;

import static com.rufino.server.constant.EmailConst.NEW_PASSWORD_MSG;
import static com.rufino.server.constant.ExceptionConst.INVALID_USER_ID;
import static com.rufino.server.constant.ExceptionConst.NOT_ENOUGH_PERMISSION;
import static com.rufino.server.constant.ExceptionConst.USER_NOT_FOUND;
import static com.rufino.server.constant.SecurityConst.DEFAULT_PASSWORD_LENGTH;
import static com.rufino.server.constant.SecurityConst.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import com.rufino.server.dao.UserDao;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.exception.domain.InvalidCredentialsException;
import com.rufino.server.model.User;
import com.rufino.server.service.EmailService;
import com.rufino.server.service.JwtTokenService;
import com.rufino.server.service.LoginCacheService;
import com.rufino.server.service.SecurityService;
import com.rufino.server.service.UserService;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private SecurityService securityService;
    private JwtTokenService jwtTokenService;
    private LoginCacheService loginCacheService;
    private EmailService emailService;

    @Autowired
    public UserServiceImpl(UserDao userDao, SecurityService securityService, JwtTokenService jwtTokenService,
            LoginCacheService loginCacheService, EmailService emailService) {
        this.userDao = userDao;
        this.securityService = securityService;
        this.jwtTokenService = jwtTokenService;
        this.loginCacheService = loginCacheService;
        this.emailService = emailService;
    }

    @Override
    public User register(User user) {
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        User savedUser = userDao.insertUser(user);
        savedUser.setPassword(null);
        emailService.send(String.format(NEW_PASSWORD_MSG, user.getUsername(), password), user.getEmail());
        return savedUser;
    }

    @Override
    public ResponseEntity<User> login(User loginUser) {
        User user = null;

        if (StringUtils.isNotBlank(loginUser.getUsername())) {
            user = userDao.getUserByUsername(loginUser.getUsername());
        } else if (StringUtils.isBlank(loginUser.getEmail()))
            throw new InvalidCredentialsException();
        else {
            user = userDao.getUserByEmail(loginUser.getEmail());
        }

        if (user == null)
            throw new InvalidCredentialsException();

        authenticate(user, loginUser.getPassword());
        HttpHeaders jwtHeaders = getJwtHeader(user);
        return new ResponseEntity<>(user, jwtHeaders, OK);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAll();
    }

    @Override
    public User getUserById(String id) {
        try {
            UUID userId = UUID.fromString(id);
            User user = userDao.getUser(userId);
            if (user == null)
                throw new ApiRequestException(USER_NOT_FOUND, NOT_FOUND);
            return user;
        } catch (IllegalArgumentException e) {
            throw new ApiRequestException(INVALID_USER_ID, BAD_REQUEST);
        }

    }

    @Override
    public boolean deleteUserById(String id) {
        try {
            UUID userId = UUID.fromString(id);
            boolean ok = userDao.deleteUser(userId);
            if (!ok)
                throw new ApiRequestException(USER_NOT_FOUND, NOT_FOUND);
            return ok;
        } catch (IllegalArgumentException e) {
            throw new ApiRequestException(INVALID_USER_ID, BAD_REQUEST);
        }
    }

    @Override
    public User updateUser(User user, String jwt) {
        verifyRole(user, jwt);
        User updated = user;
        updated = userDao.updateUser(updated);
        updated.setPassword(null);
        return updated;
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userDao.getUserByEmail(email);
        if (user == null)
            throw new ApiRequestException(USER_NOT_FOUND, NOT_FOUND);
        return user;
    }

    @Override
    public User getUserByNickname(String username) {
        User user = userDao.getUserByUsername(username);
        if (user == null)
            throw new ApiRequestException(USER_NOT_FOUND, NOT_FOUND);
        return user;
    }

    @Override
    public User saveUser(User user, MultipartFile image) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User updateProfileImg(String userId, MultipartFile image) {
        // TODO Auto-generated method stub
        return null;
    }

    private void authenticate(User user, String notEncodedPassword) {
        validateLoginOrLock(user);
        securityService.isActive(user);
        securityService.isNotLocked(user);
        securityService.verifyPassword(user, notEncodedPassword);
        user.setLastLoginDate(ZonedDateTime.now());
        userDao.updateUser(user);
    }

    private String encodePassword(String password) {
        return securityService.encodePassword(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(DEFAULT_PASSWORD_LENGTH);
    }

    private HttpHeaders getJwtHeader(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenService.createToken(user));
        return headers;
    }

    private void validateLoginOrLock(User user) {
        if (!user.isLocked()) {
            if (loginCacheService.hasExceededMaxAttempts(user.getUsername()))
                user.setLocked(true);
        } else {
            loginCacheService.evictUserFromLoginCache(user.getUsername());
        }
    }

    private void verifyRole(User user, String jwt) {
        String username = jwtTokenService.getUsername(jwt);
        User authenticatedUser = userDao.getUserByUsername(username);
        User updatingUser = userDao.getUser(user.getUserId());

        if(authenticatedUser.getRole().ordinal() < updatingUser.getRole().ordinal())
            throw new ApiRequestException(NOT_ENOUGH_PERMISSION, FORBIDDEN);
    }
}