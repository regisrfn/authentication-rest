package com.rufino.server.service.impl;

import static com.rufino.server.constant.EmailConst.EMAIL_SUBJECT;
import static com.rufino.server.constant.EmailConst.FROM_EMAIL;

import java.util.Date;

import com.rufino.server.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

    private JavaMailSender emailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void send(String message, String email) {
        SimpleMailMessage msg = createMessage(message, email);
        emailSender.send(msg);        
    }

    private SimpleMailMessage createMessage(String message, String email) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(FROM_EMAIL);
        msg.setTo(email);
        msg.setSubject(EMAIL_SUBJECT);
        msg.setText(message);
        msg.setSentDate(new Date());
        return msg;
    }
    
}
