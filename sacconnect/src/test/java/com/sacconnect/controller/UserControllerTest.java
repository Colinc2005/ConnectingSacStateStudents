package com.sacconnect.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserControllerTest {


    @Test
    void registerUser_missingRequiredFields_returnsBadRequest() {
        // we don't need the repository or email service for this branch
        UserController controller = new UserController(null, null);

        // body is Map<String, Object> in registerUser
        Map<String, Object> body = new HashMap<>();
        body.put("email", null);
        body.put("password", null);
        body.put("name", null);

        ResponseEntity<?> response = controller.registerUser(body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("email, password, and name are required", response.getBody());
    }

    @Test
    void registerUser_nonCsusEmail_returnsBadRequest() {
        UserController controller = new UserController(null, null);

        Map<String, Object> body = new HashMap<>();
        body.put("email", "student@gmail.com");      // wrong domain
        body.put("password", "secret123");
        body.put("name", "Test User");               // so the first if passes

        ResponseEntity<?> response = controller.registerUser(body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email must end with @csus.edu", response.getBody());
    }


    @Test
    void loginUser_missingEmailOrPassword_returnsBadRequest() {
        UserController controller = new UserController(null, null);

        // loginUser takes Map<String, String>
        Map<String, String> body = new HashMap<>();
        body.put("email", "student@csus.edu");
        body.put("password", null);   // triggers email/password required check

        ResponseEntity<?> response = controller.loginUser(body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("email and password are required", response.getBody());
    }
}
