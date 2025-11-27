package com.sacconnect.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String code)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("SacConnect Email Verification Code");
        message.setText("Your SacConnect verification code is: " + code + "\n\nThis code will expire in 15 minutes.");
        mailSender.send(message);
    }


    
}
