package com.rufino.server.api;

import static com.rufino.server.constant.SecurityConst.TOKEN_PREFIX;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.util.List;

import javax.validation.Valid;

import com.rufino.server.domain.HttpResponse;
import com.rufino.server.model.User;
import com.rufino.server.model.UserLogin;
import com.rufino.server.service.UserService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;

@Api("User management REST API")
@RestController
@RequestMapping(path = {"/api/v1/user"})
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        User userSaved = userService.register(user);
        return new ResponseEntity<>(userSaved, HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<User> login(@RequestBody UserLogin user) {
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
    public User selectUser(@RequestParam(name = "email", required = false) String email,
                               @RequestParam(name = "username", required = false) String username){

        if (StringUtils.isNotBlank(username))
            return userService.getUserByUsername(username);
        else
            return userService.getUserByEmail(email); 
    }

    @DeleteMapping("delete/{id}") @PreAuthorize("hasAnyAuthority('DELETE')")
    public ResponseEntity<HttpResponse> deleteUserById(@PathVariable String id) {
        return userService.deleteUserById(id);
    }

    @PutMapping("update") @PreAuthorize("hasAnyAuthority('UPDATE')")
    public User updateUser(@Valid @RequestBody User user,
                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        return userService.updateUser(user, token);
    }

    @PostMapping("save") @PreAuthorize("hasAnyAuthority('WRITE')")
    public User saveUser(@RequestPart("user") @Valid User user, 
                         @RequestParam("file") MultipartFile file) {   
        return userService.saveUser(user, file);
    }

    @PostMapping("update-profile/{id}")
    public User updateProfileImage(@PathVariable String id,
                                   @RequestParam("file") MultipartFile file) {
        return userService.updateProfileImg(id, file);
    }

    @GetMapping(path = "/image/{id}", produces = {IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE})
    public byte[] getProfileImage(@PathVariable("id") String id) {
        return userService.getProfileImage(id);
    }

}