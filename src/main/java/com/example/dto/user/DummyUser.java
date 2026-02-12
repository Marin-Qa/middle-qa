package com.example.dto.user;

public record DummyUser(int id, String firstName, String lastName, String job, String email) {
    public UserResponse toUserResponse() {
        return new UserResponse((long) id, firstName ,lastName, "QA Engineer", email);
    }
}
