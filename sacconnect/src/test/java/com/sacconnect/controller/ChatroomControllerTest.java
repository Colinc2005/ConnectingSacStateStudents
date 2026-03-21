package com.sacconnect.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sacconnect.dto.MessageDto;
import com.sacconnect.dto.UserDto;
import com.sacconnect.dto.request.CreateChatroomRequest;
import com.sacconnect.model.Chatroom;
import com.sacconnect.service.ChatroomService;
import com.sacconnect.service.MessageService;

class ChatroomControllerTest {

    private ChatroomService chatroomService;
    private MessageService messageService;
    private ChatroomController chatroomController;

    @BeforeEach
    void setUp() {
        chatroomService = mock(ChatroomService.class);
        messageService = mock(MessageService.class);
        chatroomController = new ChatroomController(chatroomService, messageService);
    }

    @Test
    void getAllChatrooms_returnsServiceResult() {
        Chatroom room = new Chatroom();
        room.setId(1L);
        room.setTitle("Room 1");
        List<Chatroom> expected = List.of(room);

        doReturn(expected).when(chatroomService).getAllChatrooms();

        List<Chatroom> actual = chatroomController.getAllChatrooms();

        assertEquals(expected, actual);
        verify(chatroomService).getAllChatrooms();
    }

    @Test
    void getChatroom_returnsServiceResponse() {
        Chatroom room = new Chatroom();
        room.setId(5L);
        room.setTitle("Test Room");
        ResponseEntity<Chatroom> expected = ResponseEntity.ok(room);

        doReturn(expected).when(chatroomService).getChatroom(5L);

        ResponseEntity<Chatroom> actual = chatroomController.getChatroom(5L);

        assertEquals(expected, actual);
        verify(chatroomService).getChatroom(5L);
    }

    @Test
    void getMessages_returnsServiceResponse() {
        MessageDto dto = new MessageDto(10L, 123L, "Alice", "Hello", null, null);
        ResponseEntity<List<MessageDto>> expected = ResponseEntity.ok(List.of(dto));

        doReturn(expected).when(chatroomService).getMessages(1L);

        ResponseEntity<List<MessageDto>> actual = chatroomController.getMessages(1L);

        assertEquals(expected, actual);
        verify(chatroomService).getMessages(1L);
    }

    @Test
    void postMessage_returnsServiceResponse() {
        MessageDto dto = new MessageDto(50L, 123L, "Alice", "Hello world", null, null);
        ResponseEntity<Object> expected = ResponseEntity.ok(dto);

        doReturn(expected).when(messageService).postMessage(1L, "Hello world", 123L, null);

        ResponseEntity<?> actual = chatroomController.postMessage(1L, "Hello world", 123L, null);

        assertEquals(expected, actual);
        verify(messageService).postMessage(1L, "Hello world", 123L, null);
    }

    @Test
    void createChatroom_returnsServiceResponse() {
        CreateChatroomRequest request = new CreateChatroomRequest();
        request.setTitle("New Room");

        Chatroom saved = new Chatroom();
        saved.setId(10L);
        saved.setTitle("New Room");

        ResponseEntity<Object> expected =
                ResponseEntity.status(HttpStatus.CREATED).body(saved);

        doReturn(expected).when(chatroomService).createChatroom(request);

        ResponseEntity<?> actual = chatroomController.createChatroom(request);

        assertEquals(expected, actual);
        verify(chatroomService).createChatroom(request);
    }

    @Test
    void getParticipants_returnsServiceResponse() {
        UserDto userDto = new UserDto(123L, "Alice", "alice@csus.edu");
        ResponseEntity<List<UserDto>> expected = ResponseEntity.ok(List.of(userDto));

        doReturn(expected).when(chatroomService).getParticipants(1L);

        ResponseEntity<List<UserDto>> actual = chatroomController.getParticipants(1L);

        assertEquals(expected, actual);
        verify(chatroomService).getParticipants(1L);
    }
}
