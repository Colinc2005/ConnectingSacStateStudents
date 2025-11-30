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

@RestController
@RequestMapping("/api/chatrooms")
@CrossOrigin(origins = "*")
public class ChatroomController {

    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatroomController(ChatroomRepository chatroomRepository,
                              MessageRepository messageRepository,
                              UserRepository userRepository) {
        this.chatroomRepository = chatroomRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    // List all chatrooms (for index.html)
    @GetMapping
    public List<Chatroom> getAllChatrooms() {
        return chatroomRepository.findAll();
    }

    // Get one chatroom
    @GetMapping("/{id}")
    public ResponseEntity<Chatroom> getChatroom(@PathVariable Long id) {
        return chatroomRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get messages for a chatroom
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long id) {
        if (!chatroomRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<Message> messages = messageRepository.findByChatroomIdOrderByCreatedAtAsc(id);
        List<MessageDto> dto = messages.stream().map(MessageDto::from).toList();
        return ResponseEntity.ok(dto);
    }

    // Post message (text + optional image)
    @PostMapping("/{id}/messages")
    public ResponseEntity<?> postMessage(
            @PathVariable Long id,
            @RequestParam(required = false) String text,
            @RequestParam Long senderId,
            @RequestParam(required = false) MultipartFile image
    ) {
        Optional<Chatroom> roomOpt = chatroomRepository.findById(id);
        if (roomOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chatroom not found");
        }

        Optional<User> userOpt = userRepository.findById(senderId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sender not found");
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = saveImage(image);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to save image");
            }
        }

        if ((text == null || text.trim().isEmpty()) && imageUrl == null) {
            return ResponseEntity.badRequest().body("Message must have text or image");
        }

        Message msg = new Message();
        msg.setChatroom(roomOpt.get());
        msg.setSender(userOpt.get());
        msg.setText(text);
        msg.setImageUrl(imageUrl);

        msg = messageRepository.save(msg);

        return ResponseEntity.ok(MessageDto.from(msg));
    }

    @PostMapping
    public ResponseEntity<?> createChatroom(@RequestBody Chatroom chatroom) {
        if (chatroom.getTitle() == null || chatroom.getTitle().trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Title is required");
        }

        chatroom.setId(null);

        Chatroom saved = chatroomRepository.save(chatroom);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<UserDto>> getParticipants(@PathVariable Long id) {
        if (!chatroomRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<User> users = messageRepository.findDistinctSendersByChatroomId(id);
        List<UserDto> dto = users.stream().map(UserDto::from).toList();

        return ResponseEntity.ok(dto);
    }


    private String saveImage(MultipartFile file) throws IOException {
        String uploadsDir = "uploads";
        Files.createDirectories(Path.of(uploadsDir));

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf('.'));
        }
        String newName = UUID.randomUUID() + ext;

        Path target = Path.of(uploadsDir, newName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // This URL must match your static resource handler
        return "/uploads/" + newName;
    }


}
