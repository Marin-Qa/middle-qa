package com.example.dto.user;

import java.util.List;

public record DummyUsersResponse(
        List<DummyUser> users,
        int skip,
        int limit,
        int total
) {}

