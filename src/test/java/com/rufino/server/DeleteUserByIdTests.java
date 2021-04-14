package com.rufino.server;

import static com.rufino.server.constant.ExceptionConst.INVALID_USER_ID;
import static com.rufino.server.constant.ExceptionConst.NOT_ENOUGH_PERMISSION;
import static com.rufino.server.constant.ExceptionConst.USER_NOT_FOUND;
import static com.rufino.server.constant.SecurityConst.FORBIDDEN_MESSAGE;
import static com.rufino.server.constant.SecurityConst.JWT_TOKEN_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

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
public class DeleteUserByIdTests {

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
        jdbcTemplate.update("DELETE FROM users_authority_list");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM authorities");
    }

    @Test
    void itShouldDeleteUserById() throws Exception{
        User admin = createSuperAdmin();
        String jwt = loginUser(admin.getUsername(),admin.getPassword());

        User user = createNewUser("arnaldo@gmail.com", "arnaldo123", "arnaldo", "rocha", "ROLE_HR");

        mockMvc.perform(delete("/api/v1/user/delete/" + user.getUserId())
                                  .header("Authorization", "Bearer " + jwt))
                                  .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Is.is("successfully operation")))
                                  .andExpect(status().isOk())
                                  .andReturn();
    }

    @Test
    public void itShouldNotDeleteUserById_NotAuthenticated() throws Exception{
        User user = createNewUser("arnaldo@gmail.com", "arnaldo123", "arnaldo", "rocha", "ROLE_HR");
        mockMvc.perform(delete("/api/v1/user/delete/" + user.getUserId()))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",Is.is(FORBIDDEN_MESSAGE)))
                .andReturn();        
    }

    @Test
    public void itShouldNotDeleteUserById_NotAllowed() throws Exception{
        User userHr = createNewUser("john@gmail.com", "john123", "John", "Doe", "ROLE_HR");
        User user = createNewUser("arnaldo@gmail.com", "arnaldo123", "arnaldo", "rocha", "ROLE_USER");

        String jwt = loginUser(userHr.getUsername(),userHr.getPassword());

         mockMvc.perform(delete("/api/v1/user/delete/" + user.getUserId())
                                  .header("Authorization", "Bearer " + jwt))
                                  .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Is.is(NOT_ENOUGH_PERMISSION)))
                                  .andExpect(status().isForbidden())
                                  .andReturn();     
    }

    @Test
    void itShouldNotDeleteUserById_invalidFormat() throws Exception{
        User admin = createSuperAdmin();
        String jwt = loginUser(admin.getUsername(),admin.getPassword());

        mockMvc.perform(delete("/api/v1/user/delete/" + "abc")
                                  .header("Authorization", "Bearer " + jwt))
                                  .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Is.is(INVALID_USER_ID)))
                                  .andExpect(status().isBadRequest())
                                  .andReturn();
    }


    @Test
    void itShouldNotDeleteUserById_userNotExists() throws Exception{
        User admin = createSuperAdmin();
        String jwt = loginUser(admin.getUsername(),admin.getPassword());

        mockMvc.perform(delete("/api/v1/user/delete/" + UUID.randomUUID())
                                  .header("Authorization", "Bearer " + jwt))
                                  .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Is.is(USER_NOT_FOUND)))
                                  .andExpect(status().isNotFound())
                                  .andReturn();
    }

    private User createSuperAdmin() {
        User user = new User();
        user.setEmail("john@gmail.com");
        user.setUsername("john123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("ROLE_SUPER_ADMIN");
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
    
}
