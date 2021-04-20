package com.rufino.server;

import static com.rufino.server.constant.SecurityConst.FORBIDDEN_MESSAGE;
import static com.rufino.server.constant.SecurityConst.JWT_TOKEN_HEADER;
import static com.rufino.server.constant.ExceptionConst.USER_NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
public class GetUserByEmailTests {

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

    @BeforeEach
    void clearTable() {
        loginCacheService.clearAll();
        jdbcTemplate.update("DELETE FROM users_authorities");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM authorities");
    }

    @Test
    void itShouldGetUserByEmail() throws Exception {

        User defaultUser = createDefaultUser();
        String jwt = loginUser(defaultUser.getUsername(),defaultUser.getPassword());

        User user = createNewUser("arnaldo@gmail.com", "arnaldo123", "arnaldo", "rocha", "ROLE_HR");

        mockMvc.perform(get("/api/v1/user/select?email=" + user.getEmail())
                                  .header("Authorization", "Bearer " + jwt))
                                  .andExpect(MockMvcResultMatchers.jsonPath("$.email",Is.is("arnaldo@gmail.com")))
                                  .andExpect(MockMvcResultMatchers.jsonPath("$.authorities",Is.is(List.of("READ","UPDATE"))))
                                  .andExpect(status().isOk())
                                  .andReturn();

    }

    @Test
    public void itShouldNotGetUserByEmail_NotAuthenticated() throws Exception{
        User user = createNewUser("arnaldo@gmail.com", "arnaldo123", "arnaldo", "rocha", "ROLE_HR");
        mockMvc.perform(get("/api/v1/user/select?email=" + user.getEmail()))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",Is.is(FORBIDDEN_MESSAGE)))
                .andReturn();        
    }

    @Test
    void itShouldGetUserByEmail_userNotFound() throws Exception {

        User defaultUser = createDefaultUser();
        String jwt = loginUser(defaultUser.getUsername(),defaultUser.getPassword());

        createNewUser("arnaldo@gmail.com", "arnaldo123", "arnaldo", "rocha", "ROLE_HR");

        mockMvc.perform(get("/api/v1/user/select?email=arnaldo2@gmail.com")
                                .header("Authorization", "Bearer " + jwt))
                    .andExpect(status().isNotFound())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message",Is.is(USER_NOT_FOUND)))
                    .andReturn(); 

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

    private User createDefaultUser() {
        User user = new User();
        user.setEmail("john@gmail.com");
        user.setUsername("john123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(securityService.encodePassword("secret123"));
        return userDao.insertUser(user);
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
