package com.sacconnect.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.sacconnect.model.Message;
import com.sacconnect.model.User;

class MessageDtoTest {

    @Test
    void from_withSender_mapsAllFieldsCorrectly() {
        User sender = new User();
        sender.setId(42L);
        sender.setName("Alice");

        Instant createdAt = Instant.now();

        Message message = new Message();
        message.setId(1L);
        message.setSender(sender);
        message.setText("Hello world");
        message.setImageUrl("/uploads/test.png");
        message.setCreatedAt(createdAt);

        MessageDto dto = MessageDto.from(message);

        assertEquals(1L, dto.id());
        assertEquals(42L, dto.senderId());
        assertEquals("Alice", dto.senderName());
        assertEquals("Hello world", dto.text());
        assertEquals("/uploads/test.png", dto.imageUrl());
        assertEquals(createdAt, dto.createdAt());
    }

    @Test
    void from_withoutSender_usesAnonymousDefaults() {
        // Arrange
        Message message = new Message();
        message.setId(2L);
        message.setSender(null);
        message.setText("No sender here");
        message.setImageUrl(null);
        message.setCreatedAt(null);

        MessageDto dto = MessageDto.from(message);

        assertEquals(2L, dto.id());
        assertNull(dto.senderId());
        assertEquals("Anonymous", dto.senderName());
        assertEquals("No sender here", dto.text());
        assertNull(dto.imageUrl());
        assertNull(dto.createdAt());
    }
}
