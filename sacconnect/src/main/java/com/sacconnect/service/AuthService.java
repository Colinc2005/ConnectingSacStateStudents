package com.sacconnect.service;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sacconnect.dto.request.LoginRequest;
import com.sacconnect.dto.request.RegisterUserRequest;
import com.sacconnect.dto.request.VerifyUserRequest;
import com.sacconnect.dto.response.AuthResponse;
import com.sacconnect.dto.response.UserResponse;
import com.sacconnect.model.User;
import com.sacconnect.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;

    public AuthService(
            UserRepository userRepository,
            EmailService emailService,
            VerificationCodeService verificationCodeService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
    }

    public ResponseEntity<?> registerUser(RegisterUserRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
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

        if (email == null || password == null || name == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("email, password, and name are required");
        }

        if (!email.toLowerCase().endsWith("@csus.edu")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email must end with @csus.edu");
        }

        Optional<User> existingOpt = userRepository.findByEmail(email);

        String code = verificationCodeService.generateVerificationCode();
        Instant expiry = verificationCodeService.generateVerificationExpiry();

        User user;

        if (existingOpt.isPresent()) {
            user = existingOpt.get();

            if (user.isVerified()) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Email is already verified. Please log in");
            }

            user.setPassword(password);
            user.setName(name);
            user.setAge(age);
            user.setMajor(major);
            user.setBio(bio);
            user.setInterests(interests);
            user.setTags(tags);
            user.setVerified(false);
            user.setVerificationCode(code);
            user.setVerificationExpiry(expiry);
        } else {
            user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setName(name);
            user.setAge(age);
            user.setMajor(major);
            user.setBio(bio);
            user.setInterests(interests);
            user.setTags(tags);
            user.setVerified(false);
            user.setVerificationCode(code);
            user.setVerificationExpiry(expiry);
        }

        userRepository.save(user);
        emailService.sendVerificationEmail(email, code);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(buildUserResponse(user));
    }

    public ResponseEntity<?> verifyUser(VerifyUserRequest request) {
        String email = request.getEmail();
        String code = request.getCode();

        if (email == null || code == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("email and code are required");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("No user found for that email");
        }

        User user = optionalUser.get();

        if (user.isVerified()) {
            return ResponseEntity.ok("Account already verified");
        }

        if (user.getVerificationCode() == null
                || user.getVerificationExpiry() == null
                || Instant.now().isAfter(user.getVerificationExpiry())
                || !user.getVerificationCode().equals(code)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired verification code");
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Email verified successfully");
    }

    public ResponseEntity<?> loginUser(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (email == null || password == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("email and password are required");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        User user = optionalUser.get();

        if (!user.getPassword().equals(password)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        if (!user.isVerified()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Verify Email before logging in");
        }

        boolean profileComplete =
                user.getAge() != null
                        && user.getMajor() != null
                        && user.getBio() != null;

        AuthResponse response = new AuthResponse();
        response.setUser(buildUserResponse(user));
        response.setProfileComplete(profileComplete);
        response.setMessage("Login successful");

        return ResponseEntity.ok(response);
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