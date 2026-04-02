package com.sacconnect.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sacconnect.model.User;
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_returnsSavedUser() {
        // create and save a user
        User user = new User();
        user.setEmail("student@csus.edu");
        user.setPassword("secret123");
        user.setName("Test User");

        User saved = userRepository.save(user);
        assertNotNull(saved.getId(), "Saved user should have an ID");

        // to look up by email
        Optional<User> foundOpt = userRepository.findByEmail("student@csus.edu");

        // Assert
        assertTrue(foundOpt.isPresent(), "User should be found by email");
        User found = foundOpt.get();
        assertEquals("student@csus.edu", found.getEmail());
        assertEquals("Test User", found.getName());
    }

    @Test
    void existsByEmail_returnsTrueForExistingEmail_andFalseForMissingEmail() {
        //saving a user
        User user = new User();
        user.setEmail("existing@csus.edu");
        user.setPassword("pw");
        user.setName("Existing User");
        userRepository.save(user);

        // does what it says
        assertTrue(userRepository.existsByEmail("existing@csus.edu"),
                "existsByEmail should be true for an existing email");

        assertFalse(userRepository.existsByEmail("missing@csus.edu"),
                "existsByEmail should be false for a non-existent email");
    }
}
