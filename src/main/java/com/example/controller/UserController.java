package com.example.controller;

import com.example.dto.user.*;
import com.example.service.UserService;
import com.example.service.UserSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users Management", description = "Агрегатор: DummyJSON → H2 БД")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserSyncService userSyncService;

    @PostMapping("/create")
    @Operation(summary = "Создать user")
    public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить user по id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Изменить user по id")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest user){
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление user по id")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Все пользователи из БД")
    public ResponseEntity<UsersResponse> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/filtered")
    @Operation(summary = "Фильтр пользователей с пагинацией",
            parameters = {
                    @Parameter(name = "domain", example = "x.dummyjson.com"),
                    @Parameter(name = "job", example = "qa"),
                    @Parameter(name = "page", example = "0"),
                    @Parameter(name = "size", example = "5"),
                    @Parameter(name = "sort", example = "id")
            })
    public ResponseEntity<UsersResponse> getFilteredUsers(@RequestParam(required = false) String domain,
                                                          @RequestParam(required = false) String firstName,
                                                          @RequestParam(required = false) String lastName,
                                                          @RequestParam(required = false) String job,
                                                          @RequestParam(required = false) Long id,
                                                          @RequestParam(defaultValue = "0") @Min(0) int page,
                                                          @RequestParam(defaultValue = "5") @Min(1) @Max(15) int size,
                                                          @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(userService.getFilteredUsers(id, firstName, lastName, job, domain, pageable));
    }

    @PostMapping("/sync")
    @Operation(summary = "Синхронизация всех юзеров из DummyJSON")
    public CompletableFuture<ResponseEntity<SyncUsersResponse>> syncUsers(@RequestParam(defaultValue = "10") int limit) {
        return userSyncService.syncUsersAsync(limit)
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    log.error("Sync failed", throwable);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }
}
