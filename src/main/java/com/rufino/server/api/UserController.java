package com.rufino.server.api;

import static com.rufino.server.constant.SecurityConst.TOKEN_PREFIX;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.rufino.server.model.User;
import com.rufino.server.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
@CrossOrigin
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<Object> saveUser(@Valid @RequestBody User user) {
        User userSaved = userService.register(user);
        return new ResponseEntity<>(userSaved, HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<User> login(@RequestBody User user) {
        return userService.login(user);
    }

    @GetMapping("get")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("get/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @GetMapping("select")
    public User getUserByEmail(@RequestParam(name = "email") String email) {
        return userService.getUserByEmail(email);
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasAnyAuthority('DELETE')")
    public ResponseEntity<Object> deleteUserById(@PathVariable String id) {
        userService.deleteUserById(id);
        Map<String, String> message = Map.of("message", "successfully operation");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("update")
    @PreAuthorize("hasAnyAuthority('UPDATE')")
    public User updateUser(@Valid @RequestBody User user, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        return userService.updateUser(user,token);
    }

}