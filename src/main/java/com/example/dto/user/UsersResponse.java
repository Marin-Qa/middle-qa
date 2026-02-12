package com.example.dto.user;

import com.example.entity.User;

import java.util.List;

public record UsersResponse(
    int page,
    int users_total,
    List<User> data
) {}