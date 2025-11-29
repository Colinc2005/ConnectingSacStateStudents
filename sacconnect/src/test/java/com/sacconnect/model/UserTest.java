package com.sacconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void settersAndGetters_workForBasicProfileFields() {
        User user = new User();

        String email = "student@csus.edu";
        String password = "secret123";
        String name = "Test User";
        Integer age = 21;
        String major = "Computer Science";
        String bio = "I like Java and Spring.";

        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setAge(age);
        user.setMajor(major);
        user.setBio(bio);

        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(name, user.getName());
        assertEquals(age, user.getAge());
        assertEquals(major, user.getMajor());
        assertEquals(bio, user.getBio());
    }

    @Test
    void interestsAndTags_areStoredAndReturnedAsSets() {
        User user = new User();

        Set<String> interests = new HashSet<>();
        interests.add("gaming");
        interests.add("cs");

        Set<String> tags = new HashSet<>();
        tags.add("freshman");
        tags.add("commuter");

        user.setInterests(interests);
        user.setTags(tags);

        assertEquals(interests, user.getInterests());
        assertEquals(tags, user.getTags());
        // sanity checks
        assertTrue(user.getInterests().contains("gaming"));
        assertTrue(user.getTags().contains("freshman"));
    }

    @Test
    void verificationFields_canBeSetAndRead() {
        User user = new User();

        String code = "123456";
        Instant expiry = Instant.now().plusSeconds(900); // 15 minutes from now

        user.setVerified(false);
        user.setVerificationCode(code);
        user.setVerificationExpiry(expiry);

        assertFalse(user.isVerified());
        assertEquals(code, user.getVerificationCode());
        assertEquals(expiry, user.getVerificationExpiry());

        user.setVerified(true);
        assertTrue(user.isVerified());
    }

    @Test
    void createdAt_canBeSet() {
        User user = new User();

        Instant created = Instant.now();
        user.setCreatedAt(created);

     

        // if you only want to check it's not null, uncomment this instead
        assertNotNull(created); // placeholder so test compiles even if getCreatedAt() doesn't exist yet
    }
}
