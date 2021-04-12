package com.rufino.server.security;

import static com.rufino.server.constant.SecurityConst.FORBIDDEN_MESSAGE;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rufino.server.domain.HttpResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2)
            throws IOException {

        HttpResponse httpResponse = new HttpResponse(
            FORBIDDEN.value(), 
            FORBIDDEN,
            FORBIDDEN.getReasonPhrase().toUpperCase(), 
            FORBIDDEN_MESSAGE);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());

        OutputStream outputStream = response.getOutputStream();
        objectMapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }

}
