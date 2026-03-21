package com.sacconnect.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sacconnect.dto.request.LoginRequest;
import com.sacconnect.dto.request.RegisterUserRequest;
import com.sacconnect.dto.request.UpdateUserProfileRequest;
import com.sacconnect.dto.request.VerifyUserRequest;
import com.sacconnect.dto.response.AuthResponse;
import com.sacconnect.dto.response.UserResponse;
import com.sacconnect.service.AuthService;
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

        ResponseEntity<Object> expectedResponse =
                ResponseEntity.status(HttpStatus.CREATED).body(userResponse);

        doReturn(expectedResponse).when(authService).registerUser(request);

        ResponseEntity<?> actualResponse = userController.registerUser(request);

        assertEquals(expectedResponse, actualResponse);
        verify(authService).registerUser(request);
    }

    @Test
    void verifyUser_returnsServiceResponse() {
        VerifyUserRequest request = new VerifyUserRequest();
        request.setEmail("verifyme@csus.edu");
        request.setCode("123456");

        ResponseEntity<Object> expectedResponse =
                ResponseEntity.ok("Email verified successfully");

        doReturn(expectedResponse).when(authService).verifyUser(request);

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

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(authResponse);

        doReturn(expectedResponse).when(authService).loginUser(request);

        ResponseEntity<?> actualResponse = userController.loginUser(request);

        assertEquals(expectedResponse, actualResponse);
        verify(authService).loginUser(request);
    }

    @Test
    void updateProfile_returnsServiceResponse() {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setId(7L);
        request.setName("Updated User");
        request.setAge(22);
        request.setMajor("Computer Science");
        request.setBio("Updated bio");
        request.setInterests(java.util.List.of("gaming", "coding"));
        request.setTags(java.util.List.of("senior"));

        UserResponse userResponse = new UserResponse();
        userResponse.setId(7L);
        userResponse.setEmail("updated@csus.edu");
        userResponse.setName("Updated User");
        userResponse.setAge(22);
        userResponse.setMajor("Computer Science");
        userResponse.setBio("Updated bio");
        userResponse.setInterests(Set.of("gaming", "coding"));
        userResponse.setTags(Set.of("senior"));
        userResponse.setVerified(true);

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(userResponse);
        doReturn(expectedResponse).when(userService).updateProfile(request);

        ResponseEntity<?> actualResponse = userController.updateProfile(request);

        assertEquals(expectedResponse, actualResponse);
        verify(userService).updateProfile(request);
    }

    @Test
    void getUserById_returnsServiceResponse() {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(5L);
        userResponse.setEmail("student@csus.edu");
        userResponse.setName("Student User");
        userResponse.setAge(21);
        userResponse.setMajor("CS");
        userResponse.setBio("Hello");
        userResponse.setInterests(Set.of("gaming"));
        userResponse.setTags(Set.of("junior"));
        userResponse.setVerified(true);

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(userResponse);
        doReturn(expectedResponse).when(userService).getUserById(5L);

        ResponseEntity<?> actualResponse = userController.getUserById(5L);

        assertEquals(expectedResponse, actualResponse);
        verify(userService).getUserById(5L);
    }

    @Test
    void deleteUser_returnsServiceResponse() {
        ResponseEntity<Object> expectedResponse =
                ResponseEntity.ok(java.util.Map.of(
                        "deletedUserId", 5L,
                        "anonymizedMessages", 3,
                        "status", "deleted"));
        doReturn(expectedResponse).when(userService).deleteUser(5L);

        ResponseEntity<?> actualResponse = userController.deleteUser(5L);

        assertEquals(expectedResponse, actualResponse);
        verify(userService).deleteUser(5L);
    }
}
