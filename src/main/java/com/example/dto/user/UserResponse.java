package com.example.dto.user;

import com.example.entity.User;

public record UserResponse (
        Long id,
        String firstName,
        String lastName,
        String job,
        String email
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getJob(),
                user.getEmail()
        );
    }
}
