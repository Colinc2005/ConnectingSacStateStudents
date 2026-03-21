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


import com.sacconnect.dto.request.LoginRequest;
import com.sacconnect.dto.request.UpdateUserProfileRequest;
import com.sacconnect.dto.request.VerifyUserRequest;
import com.sacconnect.dto.response.AuthResponse;
import com.sacconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sacconnect.model.User;
import com.sacconnect.repository.UserRepository;
import com.sacconnect.service.EmailService;
import com.sacconnect.service.AuthService;
import com.sacconnect.dto.request.RegisterUserRequest;
import com.sacconnect.dto.response.UserResponse;
import com.sacconnect.service.UserService;

class UserControllerTest {

    private AuthService authService;
    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        userService = mock(UserService.class);
        userController = new UserController(authService, userService);
    }

    @Test
    void registerUser_returnsServiceResponse() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("newuser@csus.edu");
        request.setPassword("secret123");
        request.setName("New User");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("newuser@csus.edu");
        userResponse.setName("New User");
        userResponse.setVerified(false);

        ResponseEntity<?> expectedResponse =
                ResponseEntity.status(HttpStatus.CREATED).body(userResponse);

        when(authService.registerUser(request)).thenReturn(expectedResponse);

        ResponseEntity<?> actualResponse = userController.registerUser(request);

        assertEquals(expectedResponse, actualResponse);
        verify(authService).registerUser(request);
    }

    @Test
    void verifyUser_returnsServiceResponse() {
        VerifyUserRequest request = new VerifyUserRequest();
        request.setEmail("verifyme@csus.edu");
        request.setCode("123456");

        ResponseEntity<?> expectedResponse =
                ResponseEntity.ok("Email verified successfully");

        when(authService.verifyUser(request)).thenReturn(expectedResponse);

        ResponseEntity<?> actualResponse = userController.verifyUser(request);

        assertEquals(expectedResponse, actualResponse);
        verify(authService).verifyUser(request);
    }

    @Test
    void loginUser_returnsServiceResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("student@csus.edu");
        request.setPassword("secret123");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(42L);
        userResponse.setEmail("student@csus.edu");
        userResponse.setName("Login User");
        userResponse.setAge(20);
        userResponse.setMajor("CS");
        userResponse.setBio("I like tests.");
        userResponse.setInterests(Set.of("gaming"));
        userResponse.setTags(Set.of("junior"));
        userResponse.setVerified(true);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setUser(userResponse);
        authResponse.setProfileComplete(true);
        authResponse.setMessage("Login successful");

        ResponseEntity<?> expectedResponse = ResponseEntity.ok(authResponse);

        when(authService.loginUser(request)).thenReturn(expectedResponse);

        ResponseEntity<?> actualResponse = userController.loginUser(request);

        assertEquals(expectedResponse, actualResponse);
        verify(authService).loginUser(request);
    }

    @Test
    void updateProfile_updatesExistingUser() {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setId(7L);
        request.setName("Updated User");
        request.setAge(22);
        request.setMajor("Computer Science");
        request.setBio("Updated bio");
        request.setInterests(java.util.List.of("gaming", "coding"));
        request.setTags(java.util.List.of("senior"));

        User user = new User();
        user.setId(7L);
        user.setEmail("updated@csus.edu");
        user.setPassword("secret123");
        user.setName("Old Name");
        user.setAge(20);
        user.setMajor("Old Major");
        user.setBio("Old bio");
        user.setInterests(Set.of("old"));
        user.setTags(Set.of("junior"));
        user.setVerified(true);

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));

        ResponseEntity<?> responseEntity = userController.updateProfile(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        UserResponse response = (UserResponse) responseEntity.getBody();
        assertEquals("Updated User", response.getName());
        assertEquals(22, response.getAge());
        assertEquals("Computer Science", response.getMajor());
        assertEquals("Updated bio", response.getBio());
        assertEquals(Set.of("gaming", "coding"), response.getInterests());
        assertEquals(Set.of("senior"), response.getTags());
    }

    @Test
    void updateProfile_returnsBadRequestWhenIdMissing() {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();

        ResponseEntity<?> responseEntity = userController.updateProfile(request);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("User id is required", responseEntity.getBody());
    }

    @Test
    void getUserById_returnsUserResponse() {
        User user = new User();
        user.setId(5L);
        user.setEmail("student@csus.edu");
        user.setName("Student User");
        user.setAge(21);
        user.setMajor("CS");
        user.setBio("Hello");
        user.setInterests(Set.of("gaming"));
        user.setTags(Set.of("junior"));
        user.setVerified(true);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        ResponseEntity<?> responseEntity = userController.getUserById(5L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        UserResponse response = (UserResponse) responseEntity.getBody();
        assertEquals(5L, response.getId());
        assertEquals("student@csus.edu", response.getEmail());
        assertEquals("Student User", response.getName());
        assertEquals(21, response.getAge());
        assertEquals("CS", response.getMajor());
        assertEquals("Hello", response.getBio());
        assertEquals(Set.of("gaming"), response.getInterests());
        assertEquals(Set.of("junior"), response.getTags());
    }
}
