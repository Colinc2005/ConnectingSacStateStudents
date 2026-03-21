package com.sacconnect.controller;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sacconnect.model.User;
import com.sacconnect.repository.UserRepository;
import com.sacconnect.service.EmailService;

//from request package in dto, colin chung
import com.sacconnect.dto.request.LoginRequest;
import com.sacconnect.dto.request.RegisterUserRequest;
import com.sacconnect.dto.request.UpdateUserProfileRequest;
import com.sacconnect.dto.request.VerifyUserRequest;
//from response package in dto , colin chung
import com.sacconnect.dto.response.AuthResponse;
import com.sacconnect.dto.response.UserResponse;
// service package
import com.sacconnect.service.VerificationCodeService;
import com.sacconnect.service.AuthService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private UserRepository userRepository;
    private AuthService authService;
    private UserController userController;

    public UserController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    //Registering account
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserRequest request) {
        return authService.verifyUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        return authService.loginUser(request);
    }

    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateUserProfileRequest request) {
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = opt.get();
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