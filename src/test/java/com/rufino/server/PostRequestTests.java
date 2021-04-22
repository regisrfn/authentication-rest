package com.rufino.server;

import static com.rufino.server.constant.ExceptionConst.ACCOUNT_LOCKED;
import static com.rufino.server.constant.ExceptionConst.EMAIL_NOT_AVAILABLE;
import static com.rufino.server.constant.ExceptionConst.INCORRECT_CREDENTIALS;
import static com.rufino.server.constant.ExceptionConst.INVALID_EMAIL_FORMAT;
import static com.rufino.server.constant.SecurityConst.JWT_TOKEN_HEADER;
import static com.rufino.server.constant.SecurityConst.MAXIMUM_NUMBER_OF_ATTEMPTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
@AutoConfigureMockMvc
public class PostRequestTests {

        @Autowired
        private JdbcTemplate jdbcTemplate;
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private LoginCacheService loginCacheService;
        @Autowired
        private Dotenv dotenv;
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
        void itShouldSaveUser() throws Exception {
                String EMAIL_TEST = dotenv.get("EMAIL_TEST");
                JSONObject my_obj = new JSONObject();

                MvcResult result = saveUserAndCheck(my_obj);

                User response = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
                assertThat(response.getUsername()).isEqualTo("joe123");
                assertThat(response.getEmail()).isEqualTo(EMAIL_TEST);
        }

        @Test
        void itShouldNotSaveUser_invalidEmailFormat() throws Exception {
                JSONObject my_obj = new JSONObject();

                my_obj.put("username", "john123");
                my_obj.put("password", "secret123");

                mockMvc.perform(post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.email",
                                                Is.is(INVALID_EMAIL_FORMAT)))
                                .andExpect(status().isBadRequest()).andReturn();

                my_obj.put("userEmail", "johngmail.com");
                mockMvc.perform(post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.email",
                                                Is.is(INVALID_EMAIL_FORMAT)))
                                .andExpect(status().isBadRequest()).andReturn();

        }

        @Test
        void itShouldNotSaveUser_emailAlreadyExists() throws Exception {
                JSONObject my_obj = new JSONObject();

                createDefaultUser();

                my_obj = new JSONObject();
                my_obj.put("firstName", "John");
                my_obj.put("lastName", "Doe");
                my_obj.put("username", "john1234");
                my_obj.put("email", "john@gmail.com");

                mockMvc.perform(post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.email", Is.is(EMAIL_NOT_AVAILABLE)))
                                .andExpect(status().isBadRequest()).andReturn();

        }

        @Test
        void itShouldNotSaveUser_usernameAlreadyExists() throws Exception {
                JSONObject my_obj = new JSONObject();

                createDefaultUser();

                my_obj = new JSONObject();
                my_obj.put("firstName", "John");
                my_obj.put("lastName", "Doe");
                my_obj.put("username", "john123");
                my_obj.put("email", "john2@gmail.com");

                mockMvc.perform(post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.username",
                                                Is.is(EMAIL_NOT_AVAILABLE)))
                                .andExpect(status().isBadRequest()).andReturn();

        }

        @Test
        void itShouldLoginUser() throws Exception {
                JSONObject my_obj = new JSONObject();
                createDefaultUser();

                my_obj = new JSONObject();
                my_obj.put("username", "john123");
                my_obj.put("password", "secret123");

                MvcResult mvcResult = mockMvc.perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString())).andExpect(status().isOk()).andReturn();

                User response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
                assertThat(response.getUsername()).isEqualTo("john123");
                assertThat(response.getEmail()).isEqualTo("john@gmail.com");
                assertThat(mvcResult.getResponse().getHeader(JWT_TOKEN_HEADER)).isNotBlank();

        }

        @Test
        void itShouldNotLoginUser_incorrectPassword() throws Exception {
                JSONObject my_obj = new JSONObject();
                createDefaultUser();

                my_obj = new JSONObject();
                my_obj.put("username", "john123");
                my_obj.put("password", "secret1234");

                mockMvc.perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is(INCORRECT_CREDENTIALS)))
                                .andExpect(status().isBadRequest()).andReturn();

        }

        @Test
        void itShouldNotLoginUser_userNUll() throws Exception {
                JSONObject my_obj = new JSONObject();
                createDefaultUser();

                my_obj = new JSONObject();
                mockMvc.perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is(INCORRECT_CREDENTIALS)))
                                .andExpect(status().isBadRequest()).andReturn();

        }

        @Test
        void itShouldNotLoginUser_maxAttempts() throws Exception {
                JSONObject my_obj = new JSONObject();
                createDefaultUser();

                my_obj = new JSONObject();
                my_obj.put("username", "john123");
                my_obj.put("password", "secret1234");

                for (int i = 0; i < MAXIMUM_NUMBER_OF_ATTEMPTS; i++)
                        mockMvc.perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON)
                                        .content(my_obj.toString()))
                                        .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                        Is.is(INCORRECT_CREDENTIALS)))
                                        .andExpect(status().isBadRequest()).andReturn();

                my_obj = new JSONObject();
                my_obj.put("username", "john123");
                my_obj.put("password", "secret123");
                mockMvc.perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is(ACCOUNT_LOCKED)))
                                .andExpect(status().isUnauthorized()).andReturn();

        }

        private MvcResult saveUserAndCheck(JSONObject my_obj) throws JSONException, Exception {
                my_obj.put("firstName", "John");
                my_obj.put("lastName", "Doe");
                my_obj.put("username", "joe123");
                my_obj.put("email", dotenv.get("EMAIL_TEST"));

                MvcResult result = mockMvc
                                .perform(post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON)
                                                .content(my_obj.toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Is.is("joe123")))
                                .andExpect(status().isOk()).andReturn();
                return result;
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
}
