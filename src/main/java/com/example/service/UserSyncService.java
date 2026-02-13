package com.example.service;

import com.example.dto.user.SyncUsersResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class UserSyncService {
    final
    UserService userService;

    public UserSyncService(UserService userService) {
        this.userService = userService;
    }

    @Async("syncExecutor")
    public CompletableFuture<SyncUsersResponse> syncUsersAsync(int limit) {
        log.info("Sync начат: limit={} [{}]", limit, Thread.currentThread().getName());
        return CompletableFuture.supplyAsync(
                () -> userService.syncFromDummyJSON(limit)
        );
    }
}
