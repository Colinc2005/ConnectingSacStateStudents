package com.sacconnect.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class MessageTest {

    @Test
    void message_gettersAndSetters_workCorrectly() {
        
        Message msg = new Message();

        Long expectedId = 99L;

        Chatroom chatroom = new Chatroom();
        chatroom.setId(1L);
        chatroom.setTitle("Test Room");

        User sender = new User();
        sender.setId(5L);
        sender.setName("Tester");

        String expectedText = "Hello world";
        String expectedImageUrl = "/uploads/test.png";
        Instant now = Instant.now();

        msg.setId(expectedId);
        msg.setChatroom(chatroom);
        msg.setSender(sender);
        msg.setText(expectedText);
        msg.setImageUrl(expectedImageUrl);
        msg.setCreatedAt(now);

        assertEquals(expectedId, msg.getId());
        assertEquals(chatroom, msg.getChatroom());
        assertEquals(sender, msg.getSender());
        assertEquals(expectedText, msg.getText());
        assertEquals(expectedImageUrl, msg.getImageUrl());
        assertEquals(now, msg.getCreatedAt());
    }

    @Test
    void message_createdAt_autoInitializes() {

        Message msg = new Message();

        assertNotNull(msg.getCreatedAt(), "createdAt should auto-initialize on new Message");
    }
}
