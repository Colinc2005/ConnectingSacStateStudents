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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sacconnect.model.User;
import com.sacconnect.repository.UserRepository;
import com.sacconnect.service.EmailService;

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
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> body) //requestbody converts the HTTP request (account creation) and converts it into a java object and map
    {
        
        String email = (String) body.get("email");
        String password = (String) body.get("password");
        String name = (String) body.get("name");
        Integer age = body.get("age") == null ? null : (Integer) body.get("age");
        String major = (String) body.get("major");
        String bio = (String) body.get("bio");





        @SuppressWarnings("unchecked")
        List<String> interestsList = body.get("interests") == null
                ? Collections.emptyList()
                : (List<String>) body.get("interests");
        Set<String> interests = new HashSet<>(interestsList);

        @SuppressWarnings("unchecked")
        List<String> tagsList = body.get("tags") == null
            ? Collections.emptyList()
                : (List<String>) body.get("tags");
        Set<String> tags = new HashSet<>(tagsList);
        
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

        //generating 6 digit code for verification
        String code = String.format("%06d", (int)(Math.random() * 1_000_000));
        Instant expiry = Instant.now().plusSeconds(15 * 60); //15 minutes

        User user = new User();
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

        userRepository.save(user);

        //send email
        emailService.sendVerificationEmail(email, code);


        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("name", user.getName());
        response.put("Age", user.getAge());
        response.put("major", user.getMajor());
        response.put("bio", user.getBio());
        response.put("interests", user.getInterests());
        response.put("tags", user.getTags());
        response.put("verified", user.isVerified());


        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);

    }
    //account verification
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody Map<String, String> body)
    {
        String email = body.get("email");
        String code = body.get("code");

        if (email == null || code == null)
        {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("email and code are required");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty())
        {
            return ResponseEntity 
                .status(HttpStatus.BAD_REQUEST)
                .body("No user found for that email");
        }
        
        User user = optionalUser.get();

        if (user.isVerified())
        {
            return ResponseEntity.ok("Account already verified");
        }

        if(user.getVerificationCode() == null || 
        user.getVerificationExpiry() == null || 
        Instant.now().isAfter(user.getVerificationExpiry()) || 
        !user.getVerificationCode().equals(code))
        {
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


    //login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> body)
    {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null)
        {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("email and password are required");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty())
        {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
        }

        User user = optionalUser.get();

        if (!user.getPassword().equals(password))
        {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("name", user.getName());
        response.put("Age", user.getAge());
        response.put("major", user.getMajor());
        response.put("bio", user.getBio());
        response.put("interests", user.getInterests());
        response.put("tags", user.getTags());
        response.put("verified", user.isVerified());
        return ResponseEntity.ok(response);
    }

}