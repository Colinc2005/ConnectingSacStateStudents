package com.sacconnect.dto;

import com.sacconnect.model.User;

public record UserDto(
        Long id,
        String name,
        String email
) {
    public static UserDto from(User u) {
        return new UserDto(
                u.getId(),
                u.getName(),   
                u.getEmail()
        );
    }
}