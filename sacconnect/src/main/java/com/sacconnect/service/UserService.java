package com.sacconnect.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sacconnect.dto.request.UpdateUserProfileRequest;
import com.sacconnect.dto.response.UserResponse;
import com.sacconnect.model.User;
import com.sacconnect.repository.MessageRepository;
import com.sacconnect.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public UserService(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public ResponseEntity<?> updateProfile(UpdateUserProfileRequest request) {
        Long id = request.getId();
        if (id == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("User id is required");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("User not found");
        }

        User user = optionalUser.get();

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getAge() != null) {
            user.setAge(request.getAge());
        }
        if (request.getMajor() != null) {
            user.setMajor(request.getMajor());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getInterests() != null) {
            List<String> interestsList =
                    request.getInterests() == null ? Collections.emptyList() : request.getInterests();
            Set<String> interests = new HashSet<>(interestsList);
            user.setInterests(interests);
        }
        if (request.getTags() != null) {
            List<String> tagsList =
                    request.getTags() == null ? Collections.emptyList() : request.getTags();
            Set<String> tags = new HashSet<>(tagsList);
            user.setTags(tags);
        }

        userRepository.save(user);

        return ResponseEntity.ok(buildUserResponse(user));
    }

    public ResponseEntity<?> getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        User user = optionalUser.get();
        return ResponseEntity.ok(buildUserResponse(user));
    }

    @Transactional
    public ResponseEntity<?> deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        User user = optionalUser.get();
        user.getInterests().clear();
        user.getTags().clear();
        userRepository.save(user);

        int anonymizedMessages = messageRepository.clearSenderForUser(id);
        userRepository.delete(user);

        return ResponseEntity.ok(
                Map.of(
                        "deletedUserId", id,
                        "anonymizedMessages", anonymizedMessages,
                        "status", "deleted"));
    }

    private UserResponse buildUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setAge(user.getAge());
        response.setMajor(user.getMajor());
        response.setBio(user.getBio());
        response.setInterests(user.getInterests());
        response.setTags(user.getTags());
        response.setVerified(user.isVerified());
        return response;
    }
}
