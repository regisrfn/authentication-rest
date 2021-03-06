package com.rufino.server;

import static com.rufino.server.constant.SecurityConst.JWT_TOKEN_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;

import com.rufino.server.dao.UserDao;
import com.rufino.server.model.User;
import com.rufino.server.service.LoginCacheService;
import com.rufino.server.service.SecurityService;

import org.hamcrest.core.Is;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
@AutoConfigureMockMvc
public class SaveUserTests {

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
    @Autowired
    private Dotenv dotenv;

    @BeforeEach
    void clearTable() {
        loginCacheService.clearAll();
        jdbcTemplate.update("DELETE FROM users_authorities");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM authorities");
    }

    @Test
    void itShouldSaveUser() throws Exception {
        User admin = createAdmin();
        String jwt = loginUser(admin.getUsername(),"secret123");
        JSONObject my_obj = createNewUserJSON();        

        MockMultipartFile file = new MockMultipartFile(
            "file", "index.jpeg", MediaType.IMAGE_JPEG_VALUE,
            new FileInputStream(new File("index.jpeg")));

        MockMultipartFile user = new MockMultipartFile(
                "user", "" , MediaType.APPLICATION_JSON_VALUE , my_obj.toString().getBytes());

        mockMvc.perform(multipart("/api/v1/user/save")
                            .file(file)
                            .file(user)
                            .header("Authorization", "Bearer " + jwt)
                        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Is.is("arnaldo123")))
                .andExpect(status().isOk())
                .andReturn();

    }

    private JSONObject createNewUserJSON() throws JSONException {
        String EMAIL_TEST = dotenv.get("EMAIL_TEST");
        JSONObject my_obj = new JSONObject();
        my_obj.put("firstName", "arnaldo");
        my_obj.put("lastName", "rocha");
        my_obj.put("username", "arnaldo123");
        my_obj.put("role", "ROLE_HR");
        my_obj.put("email", EMAIL_TEST);
        return my_obj;
    }

    private User createAdmin() {
        User user = new User();
        user.setEmail("john@gmail.com");
        user.setUsername("john123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("ROLE_ADMIN");
        user.setPassword(securityService.encodePassword("secret123"));
        return userDao.insertUser(user);
    }

    private String loginUser(String username, String password) throws Exception {
        JSONObject my_obj = new JSONObject();

        my_obj = new JSONObject();
        my_obj.put("username", username);
        my_obj.put("password", password);

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        String jwt = mvcResult.getResponse().getHeader(JWT_TOKEN_HEADER);

        return jwt;
    }

}
