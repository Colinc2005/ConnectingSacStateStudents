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
import com.sacconnect.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private UserRepository userRepository;
    private AuthService authService;
    private UserController userController;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
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
        return userService.updateProfile(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
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