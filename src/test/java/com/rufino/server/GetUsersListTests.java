package com.rufino.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static com.rufino.server.constant.SecurityConst.JWT_TOKEN_HEADER;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rufino.server.dao.UserDao;
import com.rufino.server.model.User;
import com.rufino.server.service.LoginCacheService;
import com.rufino.server.service.SecurityService;

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

@SpringBootTest
@AutoConfigureMockMvc
public class GetUsersListTests {

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
        jdbcTemplate.update("DELETE FROM users_authority_list");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    public void itShouldGetAllUsers() throws Exception {
        createDefaultUserList();

        JSONObject my_obj = new JSONObject();

        my_obj = new JSONObject();
        my_obj.put("username", "john123");
        my_obj.put("password", "secret123");

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        String jwt = mvcResult.getResponse().getHeader(JWT_TOKEN_HEADER);

        MvcResult result = mockMvc.perform(get("/api/v1/user/get").header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk()).andReturn();

        List<User> usersList = Arrays
                .asList(objectMapper.readValue(result.getResponse().getContentAsString(), User[].class));

        assertThat(usersList.size()).isEqualTo(2);
    }

    private List<User> createDefaultUserList() {

        User user = new User();
        user.setEmail("john@gmail.com");
        user.setUsername("john123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(securityService.encodePassword("secret123"));

        User user2 = new User();
        user2.setEmail("arnaldo@gmail.com");
        user2.setUsername("arnaldo123");
        user2.setFirstName("Arnaldo");
        user2.setLastName("Rocha");
        user2.setPassword(securityService.encodePassword("secret123"));

        userDao.insertUser(user);
        userDao.insertUser(user2);

        return userDao.getAll();
    }

}
