package com.example.service;

import com.example.dto.user.*;
import com.example.entity.*;
import com.example.exception.UserNotFoundException;
import com.example.mapping.UserMapper;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RestClient externalClient;
    private final UserMapper userMapper;

    public UserResponse createUser(UserCreateRequest request) {
        User user = userMapper.toEntity(request);
        user = userRepository.save(user);
        log.info("Сохранен пользователь: {} {}", user.getFirstName(), user.getLastName());
        return userMapper.toResponse(user);
    }

    public UsersResponse getUsers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);  // страница 0, размер = limit
        Page<User> users = userRepository.findAll(pageable);
        return new UsersResponse(0, users.getSize(), users.getContent());
    }

    public UserResponse getUserById(Long id){
        return userRepository.findById(id).map(
                UserResponse::fromEntity)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + id)
        );
    }

    public UsersResponse getFilteredUsers(Long id, String firstName, String lastName, String job, String emailDomain, Pageable pageable) {
        log.info("Фильтрация пользователей: id={}, firstName={}, lastName={}, job={}, emailDomain={}",
                id, firstName, lastName, job, emailDomain);

        Specification<User> spec = Specification.where(null);

        if (id != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("id"), id));
        }

        if (firstName != null && !firstName.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        }

        if (lastName != null && !lastName.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        }

        if (job != null && !job.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.like(cb.lower(root.get("job")), "%" + job.toLowerCase() + "%"));
        }


        if (emailDomain != null && !emailDomain.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.like(root.get("email"), "%@" + emailDomain.toLowerCase()));
        }
        // Размер страницы
        Page<User> result = userRepository.findAll(spec, pageable);

        UsersResponse response = userMapper.toUsersResponse(result);

        log.info("Найдено пользователей: {} из {}", response.data().size(), response.data());
        return response;
    }


    public SyncUsersResponse syncFromDummyJSON(Integer limit) {
        DummyUsersResponse response = externalClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users")
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .body(DummyUsersResponse.class);

        assert response != null;
        List<User> newUsers = response.users().stream()
                .map(dummy -> {
                    User user = new User();
                    user.setFirstName(dummy.firstName());
                    user.setLastName(dummy.lastName());
                    user.setJob("Dummy QA Engineer");
                    user.setEmail(dummy.email());
                    return user;
                })
                .collect(Collectors.toList());

        List<User> savedUsers = userRepository.saveAll(newUsers);
        userRepository.flush();

        List<SyncUserResponse> result = savedUsers.stream()
                .map(SyncUserResponse::fromEntity)
                .toList();

        return new SyncUsersResponse(result, savedUsers.size());
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + id));
        userMapper.updateEntityFromRequest(request, user);
        log.info("Обновлен пользователь: {} {}", user.getFirstName(), user.getLastName());
        return userMapper.toResponse(user);

    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + id));
        userRepository.delete(user);
        log.info("Удален пользователь с ID: {}", id);
    }
}
