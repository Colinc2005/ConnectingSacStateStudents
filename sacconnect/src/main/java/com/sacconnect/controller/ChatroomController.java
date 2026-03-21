package com.sacconnect.controller;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sacconnect.dto.MessageDto;
import com.sacconnect.dto.UserDto;
import com.sacconnect.model.Chatroom;
import com.sacconnect.model.Message;
import com.sacconnect.model.User;
import com.sacconnect.repository.ChatroomRepository;
import com.sacconnect.repository.MessageRepository;
import com.sacconnect.repository.UserRepository;
// request package in DTO
import com.sacconnect.dto.request.CreateChatroomRequest;
import com.sacconnect.service.ImageStorageService;
import com.sacconnect.service.MessageService;
import com.sacconnect.service.ChatroomService;

@RestController
@RequestMapping("/api/chatrooms")
@CrossOrigin(origins = "*")
public class ChatroomController {

    private final ChatroomService chatroomService;
    private final MessageService messageService;

    public ChatroomController(
            ChatroomService chatroomService,
            MessageService messageService) {
        this.chatroomService = chatroomService;
        this.messageService = messageService;

    }

    // List all chatrooms (for index.html)
    @GetMapping
    public List<Chatroom> getAllChatrooms() {
        return chatroomService.getAllChatrooms();
    }

    // Get one chatroom
    @GetMapping("/{id}")
    public ResponseEntity<Chatroom> getChatroom(@PathVariable Long id) {
        return chatroomService.getChatroom(id);
    }

    // Get messages for a chatroom
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long id) {
        return chatroomService.getMessages(id);
        }

    // Post message (text + optional image)
    @PostMapping("/{id}/messages")
    public ResponseEntity<?> postMessage(
            @PathVariable Long id,
            @RequestParam(required = false) String text,
            @RequestParam Long senderId,
            @RequestParam(required = false) MultipartFile image
    ) {
        return messageService.postMessage(id, text, senderId, image);
    }

    @PostMapping
    public ResponseEntity<?> createChatroom(@RequestBody CreateChatroomRequest request) {
        return chatroomService.createChatroom(request);
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<UserDto>> getParticipants(@PathVariable Long id) {
        return chatroomService.getParticipants(id);
    }


//    private String saveImage(MultipartFile file) throws IOException {
//        String uploadsDir = "uploads";
//        Files.createDirectories(Path.of(uploadsDir));
//
//        String originalName = file.getOriginalFilename();
//        String ext = "";
//        if (originalName != null && originalName.contains(".")) {
//            ext = originalName.substring(originalName.lastIndexOf('.'));
//        }
//        String newName = UUID.randomUUID() + ext;
//
//        Path target = Path.of(uploadsDir, newName);
//        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
//
//        return "/uploads/" + newName;
//    }

}
