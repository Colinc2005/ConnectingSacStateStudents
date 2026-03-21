package com.sacconnect.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sacconnect.dto.MessageDto;
import com.sacconnect.dto.UserDto;
import com.sacconnect.model.Chatroom;
import com.sacconnect.model.Message;
import com.sacconnect.model.User;
import com.sacconnect.repository.ChatroomRepository;
import com.sacconnect.repository.MessageRepository;
import com.sacconnect.repository.UserRepository;
import com.sacconnect.dto.request.CreateChatroomRequest;

class ChatroomControllerTest {

    private ChatroomRepository chatroomRepository;
    private MessageRepository messageRepository;
    private UserRepository userRepository;

    private ChatroomController chatroomController;

    @BeforeEach
    void setUp() {
        chatroomRepository = mock(ChatroomRepository.class);
        messageRepository = mock(MessageRepository.class);
        userRepository = mock(UserRepository.class);

        chatroomController = new ChatroomController(
                chatroomRepository,
                messageRepository,
                userRepository
        );
    }

    // all chatrooms

    @Test
    void getAllChatrooms_returnsListFromRepository() {
        Chatroom room1 = new Chatroom();
        room1.setId(1L);
        room1.setTitle("Room 1");

        when(chatroomRepository.findAll()).thenReturn(List.of(room1));

        List<Chatroom> result = chatroomController.getAllChatrooms();

        assertEquals(1, result.size());
        assertEquals("Room 1", result.get(0).getTitle());
    }

    // Getting chatroom

    @Test
    void getChatroom_returnsOkWhenFound() {
        Chatroom room = new Chatroom();
        room.setId(5L);
        room.setTitle("Test Room");

        when(chatroomRepository.findById(5L)).thenReturn(Optional.of(room));

        ResponseEntity<Chatroom> response = chatroomController.getChatroom(5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(room, response.getBody());
    }

    @Test
    void getChatroom_returnsNotFoundWhenMissing() {
        when(chatroomRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Chatroom> response = chatroomController.getChatroom(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // getting messages

    @Test
    void getMessages_returnsNotFoundIfChatroomDoesNotExist() {
        when(chatroomRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<List<MessageDto>> response = chatroomController.getMessages(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getMessages_returnsDtosWhenChatroomExists() {
        when(chatroomRepository.existsById(1L)).thenReturn(true);

        Message msg = new Message();
        msg.setId(10L);
        msg.setText("Hello");

        when(messageRepository.findByChatroomIdOrderByCreatedAtAsc(1L))
                .thenReturn(List.of(msg));

        ResponseEntity<List<MessageDto>> response = chatroomController.getMessages(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<MessageDto> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
    }

    // Post message

    @Test
    void postMessage_returnsNotFoundIfChatroomMissing() {
        when(chatroomRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = chatroomController.postMessage(
                1L,
                "Hello",
                123L,
                null
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Chatroom not found", response.getBody());
    }

    @Test
    void postMessage_returnsBadRequestIfSenderMissing() {
        Chatroom room = new Chatroom();
        room.setId(1L);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(123L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = chatroomController.postMessage(
                1L,
                "Hello",
                123L,
                null
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Sender not found", response.getBody());
    }

    @Test
    void postMessage_returnsBadRequestIfNoTextAndNoImage() {
        Chatroom room = new Chatroom();
        room.setId(1L);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(room));

        User user = new User();
        user.setId(123L);
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));

        // text is null and image is null
        ResponseEntity<?> response = chatroomController.postMessage(
                1L,
                null,
                123L,
                null
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Message must have text or image", response.getBody());
    }

    @Test
    void postMessage_withTextOnly_savesMessageAndReturnsDto() {
        Chatroom room = new Chatroom();
        room.setId(1L);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(room));

        User user = new User();
        user.setId(123L);
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));

        Message saved = new Message();
        saved.setId(50L);
        saved.setChatroom(room);
        saved.setSender(user);
        saved.setText("Hello world");

        when(messageRepository.save(any(Message.class))).thenReturn(saved);

        ResponseEntity<?> response = chatroomController.postMessage(
                1L,
                "Hello world",
                123L,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(messageRepository).save(any(Message.class));
    }

    // making chatroom 

    @Test
    void createChatroom_returnsBadRequestWhenTitleMissing() {
        CreateChatroomRequest request = new CreateChatroomRequest();
        request.setTitle("New Room");

        ResponseEntity<?> response = chatroomController.createChatroom(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Title is required", response.getBody());
    }

    @Test
    void createChatroom_savesAndReturnsCreated() {
        CreateChatroomRequest request = new CreateChatroomRequest();
        request.setTitle("New Room");

        Chatroom saved = new Chatroom();
        saved.setId(10L);
        saved.setTitle("New Room");

        when(chatroomRepository.save(any(Chatroom.class))).thenReturn(saved);

        ResponseEntity<?> response = chatroomController.createChatroom(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(saved, response.getBody());
        verify(chatroomRepository).save(any(Chatroom.class));
    }

    

    @Test
    void getParticipants_returnsNotFoundIfChatroomMissing() {
        when(chatroomRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<List<UserDto>> response = chatroomController.getParticipants(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getParticipants_returnsDtosWhenChatroomExists() {
        when(chatroomRepository.existsById(1L)).thenReturn(true);

        User u = new User();
        u.setId(123L);
        u.setName("Alice");

        when(messageRepository.findDistinctSendersByChatroomId(1L))
                .thenReturn(List.of(u));

        ResponseEntity<List<UserDto>> response = chatroomController.getParticipants(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserDto> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
    }
}
