package com.rufino.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rufino.server.constant.ExceptionConst;
import com.rufino.server.model.User;

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
public class PostRequestTests {

        @Autowired
        private JdbcTemplate jdbcTemplate;
        @Autowired
        private MockMvc mockMvc;

        private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        @BeforeEach
        void clearTable() {
                jdbcTemplate.update("DELETE FROM users");
        }

        @Test
        void itShouldSaveUser() throws Exception {
                JSONObject my_obj = new JSONObject();

                my_obj.put("username", "joe123");
                my_obj.put("password", "secret123");
                my_obj.put("email", "joe@gmail.com");

                MvcResult result = mockMvc
                                .perform(post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON)
                                                .content(my_obj.toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Is.is("joe123")))
                                .andExpect(status().isOk()).andReturn();

                User response = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
                assertThat(response.getUsername()).isEqualTo("joe123");
                assertThat(response.getEmail()).isEqualTo("joe@gmail.com");
        }

        @Test
        void itShouldNotSaveUser_invalidEmailFormat() throws Exception {
                JSONObject my_obj = new JSONObject();

                my_obj.put("username", "joe123");
                my_obj.put("password", "secret123");

                mockMvc.perform(post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.email",
                                                Is.is(ExceptionConst.INVALID_EMAIL_FORMAT)))
                                .andExpect(status().isBadRequest()).andReturn();

                my_obj.put("userEmail", "joegmail.com");
                mockMvc.perform(post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.email",
                                                Is.is(ExceptionConst.INVALID_EMAIL_FORMAT)))
                                .andExpect(status().isBadRequest()).andReturn();

        }

        @Test
        void itShouldNotSaveUser_emailAlreadyExists() throws Exception {
                JSONObject my_obj = new JSONObject();

                my_obj.put("username", "joe123");
                my_obj.put("password", "secret123");
                my_obj.put("email", "joe@gmail.com");

                mockMvc.perform(post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString())).andExpect(status().isOk()).andReturn();

                mockMvc.perform(post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.email",
                                                Is.is(ExceptionConst.EMAIL_NOT_AVAILABLE)))
                                .andExpect(status().isBadRequest()).andReturn();

        }
}
