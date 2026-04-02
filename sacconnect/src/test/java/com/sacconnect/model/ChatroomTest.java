package com.sacconnect.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ChatroomTest {

    @Test
    void chatroom_gettersAndSetters_workCorrectly() {

        Chatroom room = new Chatroom();

        Long expectedId = 10L;
        String expectedTitle = "Study Group";
        String expectedDescription = "Math 30 Discussion";
        Instant now = Instant.now();

        room.setId(expectedId);
        room.setTitle(expectedTitle);
        room.setDescription(expectedDescription);
        room.setCreatedAt(now);

        // Assert
        assertEquals(expectedId, room.getId());
        assertEquals(expectedTitle, room.getTitle());
        assertEquals(expectedDescription, room.getDescription());
        assertEquals(now, room.getCreated());
    }

    @Test
    void chatroom_createdAt_autoInitializes() {
        Chatroom room = new Chatroom();

        // getCreated should NOT be null when object is created btw
        assertNotNull(room.getCreated(), "createdAt should auto-initialize");
    }
}
