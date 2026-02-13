package com.example.mapping;


import com.example.dto.user.UserUpdateRequest;
import com.example.dto.user.UsersResponse;
import com.example.entity.User;
import com.example.dto.user.UserCreateRequest;
import com.example.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class UserMapper {
    
    public User toEntity(UserCreateRequest request) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setJob(request.job());
        user.setEmail("user" + System.currentTimeMillis() + "@test.com");
        return user;
    }
    
    public UserResponse toResponse(User entity) {
        return new UserResponse(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getJob(),
            entity.getEmail()
        );
    }

    public UsersResponse toUsersResponse(Page<User> page) {
        return new UsersResponse(
                page.getNumber(),
                (int) page.getTotalElements(),
                page.getContent()
        );
    }
    @Transactional
    public void updateEntityFromRequest(UserUpdateRequest request, User user){
        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.job() != null) user.setJob(request.job());
        if (request.email() != null) user.setEmail(request.email());
    }
}
