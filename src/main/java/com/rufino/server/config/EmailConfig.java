package com.rufino.server.config;

import static com.rufino.server.constant.EmailConst.*;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class EmailConfig {

    @Autowired
    Dotenv dotenv;

    @Bean
    public JavaMailSender getJavaMailSender() {
        String EMAIL = dotenv.get("EMAIL_SERVICE");
        String PASSWORD = dotenv.get("EMAIL_SERVICE_PASSWORD");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(GMAIL_SMTP_SERVER);
        mailSender.setPort(DEFAULT_PORT);

        mailSender.setUsername(EMAIL);
        mailSender.setPassword(PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put(TRANSPORT_PROTOCOL, "smtp");
        props.put(SMTP_AUTH, true);
        props.put(SMTP_START_TLS_ENABLE, true);
        props.put("mail.debug", "true");

        return mailSender;
    }

}
