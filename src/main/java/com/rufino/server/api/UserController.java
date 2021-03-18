package com.rufino.server.api;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.rufino.server.model.User;
import com.rufino.server.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public Map<String, String> deleteUserById(@PathVariable String id) {
        userService.deleteUserById(id);
        return Map.of("message", "successfully operation");
    }

    @PutMapping("update/{id}")
    public User updateUser(@PathVariable String id, @Valid @RequestBody User user) {
        return userService.updateUser(id, user);
    }

}