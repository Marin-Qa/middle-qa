package com.example.dto.user;

import java.util.List;

public record SyncUsersResponse(List<SyncUserResponse> users, int total) {}