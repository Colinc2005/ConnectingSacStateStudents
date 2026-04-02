package com.sacconnect.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sacconnect.dto.MessageDto;
import com.sacconnect.dto.UserDto;
import com.sacconnect.dto.request.CreateChatroomRequest;
import com.sacconnect.model.Chatroom;
import com.sacconnect.repository.ChatroomRepository;
import com.sacconnect.repository.MessageRepository;

@Service
public class ChatroomService {
    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;

    public ChatroomService(
            ChatroomRepository chatroomRepository,
            MessageRepository messageRepository) {
        this.chatroomRepository = chatroomRepository;
        this.messageRepository = messageRepository;
    }

    public List<Chatroom> getAllChatrooms() {
        return chatroomRepository.findAll();
    }

    public ResponseEntity<Chatroom> getChatroom(Long id) {
        return chatroomRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> createChatroom(CreateChatroomRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Title is required");
        }

        Chatroom chatroom = new Chatroom();
        chatroom.setTitle(request.getTitle().trim());

        Chatroom saved = chatroomRepository.save(chatroom);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    public ResponseEntity<List<MessageDto>> getMessages(Long id) {
        if (!chatroomRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<MessageDto> messages = messageRepository.findByChatroomIdOrderByCreatedAtAsc(id)
                .stream()
                .map(MessageDto::from)
                .toList();

        return ResponseEntity.ok(messages);
    }

    public ResponseEntity<List<UserDto>> getParticipants(Long id) {
        if (!chatroomRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<UserDto> participants = messageRepository.findDistinctSendersByChatroomId(id)
                .stream()
                .map(UserDto::from)
                .toList();

        return ResponseEntity.ok(participants);
    }
}