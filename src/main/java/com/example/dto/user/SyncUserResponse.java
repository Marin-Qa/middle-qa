package com.example.dto.user;

import com.example.entity.User;

public record SyncUserResponse(Long id, String firstName, String lastName, String job, String email) {
    public static SyncUserResponse fromEntity(User user) {
        return new SyncUserResponse(user.getId(), user.getFirstName(), user.getLastName(),
                user.getJob(), user.getEmail());
    }
}
