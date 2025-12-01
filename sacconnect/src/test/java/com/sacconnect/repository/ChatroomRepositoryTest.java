package com.sacconnect.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sacconnect.model.Chatroom;

@SpringBootTest
class ChatroomRepositoryTest {

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private MessageRepository messageRepository;   // this is a must

    @BeforeEach
    void cleanDatabase() {
        // Order matters so children first, then parents
        messageRepository.deleteAll();   // delete messages referencing chatrooms
        chatroomRepository.deleteAll();  // now safe to delete chatrooms
    }

    @Test
    void saveChatroom_assignsIdAndPersists() {
        Chatroom room = new Chatroom();
        room.setTitle("Study Group");
        room.setDescription("chatroom for CSC 130 study sessions");

        Chatroom saved = chatroomRepository.save(room);

        assertNotNull(saved.getId(), "Chatroom should have an ID after saving");
        assertEquals("Study Group", saved.getTitle());
        assertEquals("chatroom for CSC 130 study sessions", saved.getDescription());
    }

    @Test
    void findById_returnsSavedChatroom() {
        Chatroom room = new Chatroom();
        room.setTitle("Gaming Room");
        room.setDescription("chat about games");

        Chatroom saved = chatroomRepository.save(room);

        Chatroom found = chatroomRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found, "Chatroom should be found by ID");
        assertEquals(saved.getId(), found.getId());
        assertEquals("Gaming Room", found.getTitle());
    }

    @Test
    void findAll_returnsAllChatrooms() {
        Chatroom r1 = new Chatroom();
        r1.setTitle("Room A");

        Chatroom r2 = new Chatroom();
        r2.setTitle("Room B");

        chatroomRepository.save(r1);
        chatroomRepository.save(r2);

        List<Chatroom> rooms = chatroomRepository.findAll();

        assertEquals(2, rooms.size(), "Should return exactly 2 chatrooms");
    }
}
