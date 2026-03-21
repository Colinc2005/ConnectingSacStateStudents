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

//from request package, colin chung
import com.sacconnect.dto.request.LoginRequest;
import com.sacconnect.dto.request.RegisterUserRequest;
import com.sacconnect.dto.request.UpdateUserProfileRequest;
import com.sacconnect.dto.request.VerifyUserRequest;

import com.sacconnect.dto.response.AuthResponse;
import com.sacconnect.dto.response.UserResponse;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserController(UserRepository  userRepository, EmailService emailService)//default constructor
    {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    //Registering account
    @PostMapping("/register") //This is a method that retrieves/sends/updates/deletes data
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest request) {
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

        // Old Code
//        String email = (String) body.get("email");
//        String password = (String) body.get("password");
//        String name = (String) body.get("name");
//        Integer age = body.get("age") == null ? null : (Integer) body.get("age");
//        String major = (String) body.get("major");
//        String bio = (String) body.get("bio");

//        List<String> interestsList =
//                request.getInterests() == null ? Collections.emptyList() : request.getInterests();
//        Set<String> interests = new HashSet<>(interestsList);
//
//        List<String> tagsList =
//                request.getTags() == null ? Collections.emptyList() : request.getTags();
//        Set<String> tags = new HashSet<>(tagsList);
        
        if (email == null || password == null || name == null)
        {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("email, password, and name are required");
        }
        //hardcode requiring @csus.edu
        if (!email.toLowerCase().endsWith("@csus.edu"))
        {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Email must end with @csus.edu");
        }


        //fixes duplicate emails to send code
        Optional<User> existingOpt = userRepository.findByEmail(email);

        //6digit verificaiton code
        String code = String.format("%06d", (int) (Math.random() * 1_000_000));
        Instant expiry = Instant.now().plusSeconds(15 * 60);

        User user;

        if (existingOpt.isPresent()) {
            user = existingOpt.get();
            if (user.isVerified())
            {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email is already verified. Please log in");
            }
            //updates info and resends code if not verified
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
        else
        {
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
        //send email
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(buildUserResponse(user));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserRequest request) {
        System.out.println("VERIFY endpoint hit with email: " + request.getEmail());

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

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
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
    // helper method to build UserResponse from User
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