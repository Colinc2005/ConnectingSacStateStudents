package com.sacconnect.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class EmailServiceTest {

    @Test
    void sendVerificationEmail_buildsCorrectMessageAndCallsMailSender() {
        // create a mail sender
        JavaMailSender mockMailSender = mock(JavaMailSender.class);

        // Use the mail in our service
        EmailService emailService = new EmailService(mockMailSender);

        String to = "student@csus.edu";
        String code = "123456";

        // call the method we are testing
        emailService.sendVerificationEmail(to, code);

        // Capture what was sent to mailSender.send
        ArgumentCaptor<SimpleMailMessage> messageCaptor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Verify send was called exactly once and grab the message
        verify(mockMailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        // TO address is correct
        assertArrayEquals(new String[] { to }, sentMessage.getTo());

        // subject is correct
        assertEquals("SacConnect Email Verification Code", sentMessage.getSubject());

        // body contains the code and the expire message
        String text = sentMessage.getText();
        assertTrue(text.contains(code));
        assertTrue(text.contains("This code will expire in 15 minutes."));
    }
}
