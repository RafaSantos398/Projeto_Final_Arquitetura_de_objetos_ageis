package com.berxy.projeto.user.dto;

import com.berxy.projeto.user.Role;
import com.berxy.projeto.user.User;

import java.time.Instant;

public record UserResponse(
        Long id,
        String nome,
        String email,
        Role role,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}