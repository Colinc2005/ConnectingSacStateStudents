package com.sacconnect.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sacconnect.dto.request.UpdateUserProfileRequest;
import com.sacconnect.dto.response.UserResponse;
import com.sacconnect.model.User;
import com.sacconnect.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        String name = request.getName();
        Integer age = request.getAge();
        String major = request.getMajor();
        String bio = request.getBio();

        List<String> interestsList =
                request.getInterests() == null ? Collections.emptyList() : request.getInterests();
        Set<String> interests = new HashSet<>(interestsList);

        List<String> tagsList =
                request.getTags() == null ? Collections.emptyList() : request.getTags();
        Set<String> tags = new HashSet<>(tagsList);

        user.setName(name);
        user.setAge(age);
        user.setMajor(major);
        user.setBio(bio);
        user.setInterests(interests);
        user.setTags(tags);

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