package com.rufino.server;

import static com.rufino.server.constant.ExceptionConst.NOT_ENOUGH_PERMISSION;
import static com.rufino.server.constant.SecurityConst.FORBIDDEN_MESSAGE;
import static com.rufino.server.constant.SecurityConst.JWT_TOKEN_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rufino.server.dao.UserDao;
import com.rufino.server.model.User;
import com.rufino.server.service.LoginCacheService;
import com.rufino.server.service.SecurityService;

import org.hamcrest.core.Is;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
public class UpdateUserTests {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LoginCacheService loginCacheService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private SecurityService securityService;
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void clearTable() {
        loginCacheService.clearAll();
        jdbcTemplate.update("DELETE FROM users_authorities");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM authorities");
    }

    @Test
    void itShouldUpdateUser() throws Exception{
        User manager = createManager();
        String jwt = loginUser(manager.getUsername(),manager.getPassword());

        User user = createNewUser("arnaldo@gmail.com", "arnaldo123", "arnaldo", "rocha", "ROLE_HR");

        String jsonUser = objectMapper.writeValueAsString(user);
        JSONObject updatedUser = new JSONObject(jsonUser);
        updatedUser.put("role", "ROLE_USER");

        mockMvc.perform(put("/api/v1/user/update/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedUser.toString())
                                .header("Authorization", "Bearer " + jwt))
                    .andExpect(status().isOk())
                    .andReturn();
    }

    @Test
    public void itShouldNotUpdateUser_NotAuthenticated() throws Exception{
        User user = createNewUser("arnaldo@gmail.com", "arnaldo123", "arnaldo", "rocha", "ROLE_HR");

        String jsonUser = objectMapper.writeValueAsString(user);
        JSONObject updatedUser = new JSONObject(jsonUser);
        updatedUser.put("role", "ROLE_USER");

        mockMvc.perform(put("/api/v1/user/update/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedUser.toString()))
                    .andExpect(status().isForbidden())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message",Is.is(FORBIDDEN_MESSAGE)))
                    .andReturn();       
    }

    @Test
    public void itShouldNotUpdateUser_notEnoughPermission() throws Exception{    
        User defaultUser = createDefaultUser();
        String jwt = loginUser(defaultUser.getUsername(),defaultUser.getPassword());

        User user = createNewUser("arnaldo@gmail.com", "arnaldo123", "arnaldo", "rocha", "ROLE_HR");

        String jsonUser = objectMapper.writeValueAsString(user);
        JSONObject updatedUser = new JSONObject(jsonUser);
        updatedUser.put("role", "ROLE_USER");

        mockMvc.perform(put("/api/v1/user/update/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedUser.toString())
                                .header("Authorization", "Bearer " + jwt))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        Is.is(NOT_ENOUGH_PERMISSION)))
                    .andExpect(status().isForbidden())
                    .andReturn(); 
    }

    @Test
    void itShouldNotUpdateUser_RoleNotEnough() throws Exception{
        User manager = createManager();
        String jwt = loginUser(manager.getUsername(),manager.getPassword());

        User user = createNewUser("arnaldo@gmail.com", "arnaldo123", "arnaldo", "rocha", "ROLE_SUPER_ADMIN");

        String jsonUser = objectMapper.writeValueAsString(user);
        JSONObject updatedUser = new JSONObject(jsonUser);
        updatedUser.put("role", "ROLE_USER");

        mockMvc.perform(put("/api/v1/user/update/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedUser.toString())
                                .header("Authorization", "Bearer " + jwt))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        Is.is(NOT_ENOUGH_PERMISSION)))
                    .andExpect(status().isForbidden())
                    .andReturn();
    }

    private User createManager() {
        User user = new User();
        user.setEmail("john@gmail.com");
        user.setUsername("john123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("ROLE_MANAGER");
        user.setPassword(securityService.encodePassword("secret123"));
        return userDao.insertUser(user);
    }

    private User createDefaultUser() {
        User user = new User();
        user.setEmail("john@gmail.com");
        user.setUsername("john123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(securityService.encodePassword("secret123"));
        return userDao.insertUser(user);
    }

    private String loginUser(String username, String password) throws Exception {
        JSONObject my_obj = new JSONObject();

        my_obj = new JSONObject();
        my_obj.put("username", "john123");
        my_obj.put("password", "secret123");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        String jwt = mvcResult.getResponse().getHeader(JWT_TOKEN_HEADER);

        return jwt;
    }

    private User createNewUser(String email, String username, String firstName,String lastName,String role) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(securityService.encodePassword("secret123"));
        user.setRole(role);
        return userDao.insertUser(user);
    }
}
