package com.example.controller;

import com.example.dto.user.SyncUsersResponse;
import com.example.dto.user.UserCreateRequest;
import com.example.dto.user.UserResponse;
import com.example.dto.user.UsersResponse;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users Management", description = "Агрегатор: DummyJSON → H2 БД")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @Operation(summary = "Создать пользователя")
    public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/{id}")
    @Operation(summary = "Получить пользователя по id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    @Operation(summary = "Все пользователи из БД")
    public ResponseEntity<UsersResponse> getUsers(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(userService.getUsers(page));
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
                                                          @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size,
                                                          @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(userService.getFilteredUsers(id, firstName, lastName, job, domain, pageable));
    }

    @PostMapping("/sync")
    @Operation(summary = "Синхронизация всех юзеров из DummyJSON")
    public ResponseEntity<SyncUsersResponse> syncUsers(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(userService.syncFromDummyJSON(limit));
    }
}
