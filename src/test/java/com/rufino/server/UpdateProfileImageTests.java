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


@SpringBootTest
@AutoConfigureMockMvc
public class UpdateProfileImageTests {

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
    void itShouldUpdateUser() throws Exception {
        User admin = createAdmin();
        String jwt = loginUser(admin.getUsername(),"secret123");
        User user = createNewUser("arnaldo@gmail.com", "arnaldo123", "arnaldo", "rocha", "ROLE_HR");        

        MockMultipartFile file = new MockMultipartFile(
            "file", "index.jpeg", MediaType.IMAGE_JPEG_VALUE,
            new FileInputStream(new File("index.jpeg")));

        mockMvc.perform(multipart("/api/v1/user/update-profile/" + user.getUserId())
                            .file(file)
                            .header("Authorization", "Bearer " + jwt)
                        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.profileImageUrl").exists())
                .andExpect(status().isOk())
                .andReturn();

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
