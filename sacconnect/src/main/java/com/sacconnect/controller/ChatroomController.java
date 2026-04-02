package com.sacconnect.controller;

import java.util.List;

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

// request package in DTO
import com.sacconnect.dto.request.CreateChatroomRequest;
// service package
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
