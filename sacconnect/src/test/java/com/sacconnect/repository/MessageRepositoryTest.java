package com.sacconnect.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sacconnect.model.Chatroom;
import com.sacconnect.model.Message;
import com.sacconnect.model.User;

@SpringBootTest
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        // child table first, then parents (FK constraint)
        messageRepository.deleteAll();
        chatroomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByChatroomIdOrderByCreatedAtAsc_returnsMessagesInOrder() {

        Chatroom room1 = new Chatroom();
        room1.setTitle("Room 1");
        room1.setDescription("First room");
        room1 = chatroomRepository.save(room1);

        Chatroom room2 = new Chatroom();
        room2.setTitle("Room 2");
        room2.setDescription("Second room");
        room2 = chatroomRepository.save(room2);

        User sender = new User();
        sender.setEmail("student@csus.edu");
        sender.setPassword("pw");
        sender.setName("Test User");
        sender = userRepository.save(sender);

        Instant t1 = Instant.now().minusSeconds(60);
        Instant t2 = Instant.now().minusSeconds(30);
        Instant t3 = Instant.now();

        Message m1 = new Message();
        m1.setChatroom(room1);
        m1.setSender(sender);
        m1.setText("first");
        m1.setCreatedAt(t1);

        Message m2 = new Message();
        m2.setChatroom(room1);
        m2.setSender(sender);
        m2.setText("second");
        m2.setCreatedAt(t2);

        Message m3 = new Message();
        m3.setChatroom(room1);
        m3.setSender(sender);
        m3.setText("third");
        m3.setCreatedAt(t3);

        // message in a different chatroom 
        Message other = new Message();
        other.setChatroom(room2);
        other.setSender(sender);
        other.setText("other room");

        messageRepository.save(m1);
        messageRepository.save(m2);
        messageRepository.save(m3);
        messageRepository.save(other);

       
        List<Message> result =
                messageRepository.findByChatroomIdOrderByCreatedAtAsc(room1.getId());

       
        assertEquals(3, result.size(), "Should return 3 messages for room1");

        // order by createdAt ascending first, second, third
        assertEquals("first",  result.get(0).getText());
        assertEquals("second", result.get(1).getText());
        assertEquals("third",  result.get(2).getText());
    }

    @Test
    void findDistinctSendersByChatroomId_returnsUniqueSendersForChatroom() {
       
        Chatroom room = new Chatroom();
        room.setTitle("Main Room");
        room.setDescription("Chat");
        room = chatroomRepository.save(room);

        Chatroom otherRoom = new Chatroom();
        otherRoom.setTitle("Other");
        otherRoom.setDescription("Other chat");
        otherRoom = chatroomRepository.save(otherRoom);

        User u1 = new User();
        u1.setEmail("u1@csus.edu");
        u1.setPassword("pw1");
        u1.setName("User One");
        u1 = userRepository.save(u1);

        User u2 = new User();
        u2.setEmail("u2@csus.edu");
        u2.setPassword("pw2");
        u2.setName("User Two");
        u2 = userRepository.save(u2);

        User u3 = new User();
        u3.setEmail("u3@csus.edu");
        u3.setPassword("pw3");
        u3.setName("User Three");
        u3 = userRepository.save(u3);

        // room messages: u1 twice, u2 once
        Message m1 = new Message();
        m1.setChatroom(room);
        m1.setSender(u1);
        m1.setText("hi from u1");

        Message m2 = new Message();
        m2.setChatroom(room);
        m2.setSender(u1);
        m2.setText("another from u1");

        Message m3 = new Message();
        m3.setChatroom(room);
        m3.setSender(u2);
        m3.setText("hello from u2");

        // message for different room, different sender 
        Message other = new Message();
        other.setChatroom(otherRoom);
        other.setSender(u3);
        other.setText("other room message");

        messageRepository.save(m1);
        messageRepository.save(m2);
        messageRepository.save(m3);
        messageRepository.save(other);

      
        List<User> senders = messageRepository.findDistinctSendersByChatroomId(room.getId());

      
        assertEquals(2, senders.size(), "Should return 2 distinct senders for this room");

        Set<Long> senderIds = senders.stream().map(User::getId).collect(Collectors.toSet());

        assertTrue(senderIds.contains(u1.getId()), "u1 should be included");
        assertTrue(senderIds.contains(u2.getId()), "u2 should be included");
        // u3 should NOT be in the result
        assertTrue(!senderIds.contains(u3.getId()), "u3 should not be included");
    }
}
