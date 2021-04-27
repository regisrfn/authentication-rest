package com.rufino.server;

import static com.rufino.server.constant.SecurityConst.FORBIDDEN_MESSAGE;
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
        User user = createDefaultUser();
        String jwt = loginUser(user.getUsername(),"secret123");   

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

    @Test
    public void itShouldNotUpdateUser_NotAuthenticated() throws Exception{
        User user = createDefaultUser();

        MockMultipartFile file = new MockMultipartFile(
            "file", "index.jpeg", MediaType.IMAGE_JPEG_VALUE,
            new FileInputStream(new File("index.jpeg")));

            mockMvc.perform(multipart("/api/v1/user/update-profile/" + user.getUserId())
                                .file(file))
                    .andExpect(status().isForbidden())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message",Is.is(FORBIDDEN_MESSAGE)))
                    .andReturn();     
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
        my_obj.put("username", username);
        my_obj.put("password", password);

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        String jwt = mvcResult.getResponse().getHeader(JWT_TOKEN_HEADER);

        return jwt;
    }    
}
