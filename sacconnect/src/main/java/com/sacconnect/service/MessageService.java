package com.sacconnect.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sacconnect.dto.MessageDto;
import com.sacconnect.model.Chatroom;
import com.sacconnect.model.Message;
import com.sacconnect.model.User;
import com.sacconnect.repository.ChatroomRepository;
import com.sacconnect.repository.MessageRepository;
import com.sacconnect.repository.UserRepository;

@Service
public class MessageService {
    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    public MessageService(
            ChatroomRepository chatroomRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            ImageStorageService imageStorageService) {
        this.chatroomRepository = chatroomRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
    }

    public ResponseEntity<?> postMessage(
            Long chatroomId,
            String text,
            Long senderId,
            MultipartFile image) {
        Optional<Chatroom> roomOpt = chatroomRepository.findById(chatroomId);
        if (roomOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Chatroom not found");
        }

        Optional<User> userOpt = userRepository.findById(senderId);
        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Sender not found");
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = imageStorageService.saveImage(image);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to save image");
            }
        }

        if ((text == null || text.trim().isEmpty()) && imageUrl == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Message must have text or image");
        }

        Message message = new Message();
        message.setChatroom(roomOpt.get());
        message.setSender(userOpt.get());
        message.setText(text);
        message.setImageUrl(imageUrl);

        message = messageRepository.save(message);
        return ResponseEntity.ok(MessageDto.from(message));
    }
}