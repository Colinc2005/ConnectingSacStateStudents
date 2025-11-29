package com.sacconnect.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sacconnect.model.User;
import com.sacconnect.repository.UserRepository;
import com.sacconnect.service.EmailService;

class UserControllerTest {

    private UserRepository userRepository;
    private EmailService emailService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        emailService = mock(EmailService.class);
        userController = new UserController(userRepository, emailService);
    }

    @Test
    void registerUser_createsNewUnverifiedUser_andSendsEmail() {
        // Arrange
        Map<String, Object> body = new HashMap<>();
        body.put("email", "newuser@csus.edu");
        body.put("password", "secret123");
        body.put("name", "New User");
        body.put("age", 21);
        body.put("major", "Computer Science");
        body.put("bio", "Hi, I am new here!");
        body.put("interests", java.util.List.of("gaming", "cs"));
        body.put("tags", java.util.List.of("freshman"));

        // no existing user with this email
        when(userRepository.findByEmail("newuser@csus.edu"))
                .thenReturn(Optional.empty());

        // simulate DB assigning an ID
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);                 // pretending the DB generated ID = 1 because of an issue saving ID 
            return u;
        });

      
        ResponseEntity<?> responseEntity = userController.registerUser(body);

     
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) responseEntity.getBody();
        assertNotNull(response);
        assertEquals(1L, response.get("id"));
        assertEquals("newuser@csus.edu", response.get("email"));
        assertEquals("New User", response.get("name"));
        assertFalse((Boolean) response.get("verified"));

        // verify that an email was sent
        verify(emailService, times(2)).sendVerificationEmail(eq("newuser@csus.edu"), anyString());        // verify a user was saved
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_rejectsNonCsusEmail() {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "someone@gmail.com");
        body.put("password", "pw");
        body.put("name", "Bad Email User");

        ResponseEntity<?> responseEntity = userController.registerUser(body);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Email must end with @csus.edu", responseEntity.getBody());
    }

    @Test
    void registerUser_missingRequiredFields_returnsBadRequest() {
        Map<String, Object> body = new HashMap<>();
        // no email / password / name

        ResponseEntity<?> responseEntity = userController.registerUser(body);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("email, password, and name are required", responseEntity.getBody());
    }

    @Test
    void verifyUser_successfullyVerifiesUserWithCorrectCode() {
        // Arrange
        String email = "verifyme@csus.edu";
        String code = "123456";

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("code", code);

        User user = new User();
        user.setEmail(email);
        user.setVerified(false);
        user.setVerificationCode(code);
        user.setVerificationExpiry(Instant.now().plusSeconds(900));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        
        ResponseEntity<?> responseEntity = userController.verifyUser(body);

        
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Email verified successfully", responseEntity.getBody());
        assertTrue(user.isVerified());
        // after verification controller should clear code/expiry
        assertEquals(null, user.getVerificationCode());
        assertEquals(null, user.getVerificationExpiry());
        verify(userRepository).save(user);
    }

    @Test
    void verifyUser_withInvalidCode_returnsBadRequest() {
        String email = "verifyme@csus.edu";

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("code", "WRONG");

        User user = new User();
        user.setEmail(email);
        user.setVerified(false);
        user.setVerificationCode("123456");
        user.setVerificationExpiry(Instant.now().plusSeconds(900));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ResponseEntity<?> responseEntity = userController.verifyUser(body);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid or expired verification code", responseEntity.getBody());
    }

    @Test
    void loginUser_successfulLoginReturnsUserData() {
        String email = "student@csus.edu";

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", "secret123");

        User user = new User();
        user.setId(42L);
        user.setEmail(email);
        user.setPassword("secret123");
        user.setName("Login User");
        user.setAge(20);
        user.setMajor("CS");
        user.setBio("I like tests.");
        user.setInterests(Set.of("gaming"));
        user.setTags(Set.of("junior"));
        user.setVerified(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ResponseEntity<?> responseEntity = userController.loginUser(body);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) responseEntity.getBody();
        assertNotNull(response);
        assertEquals(42L, response.get("id"));
        assertEquals(email, response.get("email"));
        assertEquals("Login User", response.get("name"));
        assertEquals(20, response.get("Age"));
        assertEquals("CS", response.get("major"));
        assertEquals("I like tests.", response.get("bio"));
        assertEquals(Set.of("gaming"), response.get("interests"));
        assertEquals(Set.of("junior"), response.get("tags"));
        assertTrue((Boolean) response.get("verified"));
    }

    @Test
    void loginUser_invalidPasswordReturnsUnauthorized() {
        String email = "student@csus.edu";

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", "wrongPW");

        User user = new User();
        user.setEmail(email);
        user.setPassword("correctPW");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ResponseEntity<?> responseEntity = userController.loginUser(body);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Invalid email or password", responseEntity.getBody());
    }

    @Test
    void loginUser_missingFieldsReturnsBadRequest() {
        Map<String, String> body = new HashMap<>();
        // no email / password

        ResponseEntity<?> responseEntity = userController.loginUser(body);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("email and password are required", responseEntity.getBody());
    }
}
