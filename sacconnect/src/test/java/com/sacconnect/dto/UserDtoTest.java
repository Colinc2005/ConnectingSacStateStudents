package com.sacconnect.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.sacconnect.model.User;

class UserDtoTest {

    @Test
    void from_mapsFieldsCorrectly() {
        User user = new User();
        user.setId(42L);
        user.setName("Test User");
        user.setEmail("test@csus.edu");

        UserDto dto = UserDto.from(user);

        assertEquals(42L, dto.id());
        assertEquals("Test User", dto.name());
        assertEquals("test@csus.edu", dto.email());
    }
}
