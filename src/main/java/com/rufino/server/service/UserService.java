package com.rufino.server.service;

import java.util.List;

import com.rufino.server.model.User;
import com.rufino.server.model.UserLogin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    public User register(User user);

    public ResponseEntity<User> login(UserLogin userLogin);

    public List<User> getAllUsers();

    public User getUserById(String id);

    public boolean deleteUserById(String id);

    public User updateUser(User user, String jwt);

    public User getUserByEmail(String email);

    public User getUserByUsername(String username);

    public User saveUser(User user, MultipartFile file);

    public User updateProfileImg(String userId, MultipartFile file);

    public byte[] getProfileImage(String userId);


}
